package org.jboss.jawabot.plugin.jira.irc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.jboss.jawabot.ex.JawaBotException;
import org.jboss.jawabot.irc.IIrcPluginHook;
import org.jboss.jawabot.irc.IrcBotProxy;
import org.jboss.jawabot.irc.IrcPluginException;
import org.jboss.jawabot.irc.IrcPluginHookBase;
import org.jboss.jawabot.irc.ent.IrcEvMessage;
import org.jboss.jawabot.plugin.jira.config.beans.JiraPluginConfigBean;
import org.jboss.jawabot.plugin.jira.config.beans.RepositoryBean;
import org.jboss.jawabot.plugin.jira.core.ChannelsStatusStore;
import org.jboss.jawabot.plugin.jira.core.IssueShownInfo;
import org.jboss.jawabot.plugin.jira.core.JiraPlugin;
import org.jboss.jawabot.plugin.jira.core.TimeoutCache;
import org.jboss.jawabot.plugin.jira.repo2.RepositoriesManager;
import org.jboss.jawabot.plugin.jira.scrapers.IssueInfo;
import org.jboss.jawabot.plugin.jira.scrapers.ScrapersManager;
import org.jboss.jawabot.plugin.jira.scrapers.ScrapingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Former JiraBot.
 *  Scans messages for JIRA IDs and when spots one, prints basic info about it.
 * 
 *  @author Ondrej Zizka
 */
public class JiraIrcPluginHook extends IrcPluginHookBase implements IIrcPluginHook<Object> {
    private static final Logger log = LoggerFactory.getLogger( JiraIrcPluginHook.class );



    //final String JIRA_KEY_REGEX = "([A-Z]{2,}\\-[0-9]+)";
    private static final String JIRA_KEY_REGEX = "((?<![-_.A-Z])[A-Z0-9]{$minChars,}-[0-9]++)(?!(-|\\.[0-9A-Za-z]))";
    private static final int MIN_ISSUE_PREFIX_LEN = 2;
    private static Pattern JIRA_KEY_PATTERN = Pattern.compile( JIRA_KEY_REGEX.replace("$minChars",  "" + MIN_ISSUE_PREFIX_LEN) );
    //private static Pattern JIRA_KEY_PATTERN = Pattern.compile("([A-Z]{3,}\\-[0-9]+(?!\\.))"); // Not followed by "." -> Bug: "ABC-123." treated as "ABC-12".

    //private static final int MAX_JIRA_IDS_PER_REQUEST = 3;       // TODO: Move to the configuration.
    //private static final int DEFAULT_REPEAT_DELAY_SECONDS = 300;         // TODO: Move to the configuration.
    private static final int DEFAULT_CACHED_ISSUES_TIMEOUT_MINUTES = 60; // TODO: Move to the configuration.


    //@Inject private JiraPluginConfigBean config = null;
    //@Inject private JawaBot jawaBot;

    /**
     * This will receive app-scoped plugin bean which keeps the loaded config.
     */
    @Inject JiraPlugin jiraPlugin;


    private final RepositoriesManager repoManager = new RepositoriesManager();

    private final ChannelsStatusStore channelStatusStore = new ChannelsStatusStore();

    private final TimeoutCache<IssueInfo> issueCache = new TimeoutCache( DEFAULT_CACHED_ISSUES_TIMEOUT_MINUTES * 60 * 1000 /**/ );





    /**
     *   Trigger applyConfig() on init.
     */
    @Override
    public void initModule(Object initObject) throws JawaBotException {
        super.initModule(initObject);
        this.applyConfig();
    }






    /**
     *   Apply the data from the config to this JiraBot.
     *   That means, join to channels not currently joined, etc.
     */
    private void applyConfig() {

        JiraPluginConfigBean config = this.jiraPlugin.getConfig();

        JIRA_KEY_PATTERN = Pattern.compile( JIRA_KEY_REGEX.replace("$minChars", ""+config.settings.minJiraPrefixLength) );


        // Cache timeout.
        this.issueCache.setTimeoutMS( config.settings.cacheTimeoutMinutes * 60 * 1000 );


        //this.repoManager.setMinJiraPrefixLength( config.settings.minJiraPrefixLength );///
        this.repoManager.setIgnoredPrefixes( config.jira.ignoredPrefixes );

        // Ignored prefixes.
        this.repoManager.setIgnoredPrefixes( config.jira.ignoredPrefixes );

        // Default repository type.
        this.repoManager.setDefaultRepoType( config.jira.defaultType );

        // (Re-)create prefix -> repository map.
        this.repoManager.clearRepos();

        //  <repository name="redhat-bugzilla" url="https://bugzilla.redhat.com/show_bug.cgi?id=" type="bugzilla34">
        for( RepositoryBean repo : config.jira.repositories ){

            log.debug("Processing repo: "+ repo);

            // Default type config.jira.
            if( repo.getType() == null )
            repo.setType( this.repoManager.getDefaultRepoType() );

            // Scraper
            repo.setScraper( ScrapersManager.getScraperForRepoType( repo.getType() ) );

            // Add the repo.
            this.repoManager.addRepo(repo);

            log.debug("  Processed repo: "+ repo);
        }

    }// applyConfig()






    // IRC stuff.


    @Override
    public void onMessage( IrcEvMessage ev, IrcBotProxy bot ) throws IrcPluginException {

        // Either process a command or search for Jira IDs.
        boolean wasCommand = false;


        String msgNormalized = ev.getText().trim().toLowerCase();

        // Check for presence of bot nick prolog.
        boolean startsWithBotNick = msgNormalized.startsWith( bot.getNick().toLowerCase() );

        // If the prolog is present,
        if( startsWithBotNick ) {

            // remove it,
            int prologEnd = bot.getNick().length();

            String command = msgNormalized.substring( prologEnd );
            command = StringUtils.removeStart( command, ":" ).trim();

            // and process the command.
            wasCommand = handleJiraBotCommand( ev.getChannel(), command, false, bot );

        }


        // Not a command?  Search for Jira IDs.
        if( !wasCommand ) {
            this.handleJiraRequest( ev.getChannel(), ev.getText().trim(), bot );
        }
    }// onMessage()



    @Override
    public void onPrivateMessage(IrcEvMessage ev, IrcBotProxy bot) throws IrcPluginException {
            this.handleJiraBotCommand( ev.getUser(), ev.getText().trim(), true, bot );
    }








   /**
    * Searches for Jira IDs in the message and replies with the Jira info.
    *
    * If there are more IDs than MAX_JIRA_IDS_PER_REQUEST, doesn't reply.
    *
    * @param from     Who did this message come from - user or #channel
    * @param msgText
    *
    * TODO: Very ugly code, refactor.
    */
   void handleJiraRequest( String from, String msgText, IrcBotProxy bot )
   {
      // Parse the JIRA IDs.
      List<String> jiraIDs = new ArrayList(); // List to keep the order of issues.
      Matcher jiraIdMatcher = JIRA_KEY_PATTERN.matcher( msgText );
      while( jiraIdMatcher.find() ) {
         jiraIDs.add( jiraIdMatcher.group().toUpperCase() );
      }

      // No issues found.
      if( jiraIDs.size() == 0 ) return;

      // At most X jira requests in one messge.
      //if( jiraIDs.size() > this.jawaBot.getConfig().getPluginsMap().get("jira").settings.repeatDelayMessages ){
      if( jiraIDs.size() > this.jiraPlugin.getConfig().settings.repeatDelayMessages ){
         bot.sendMessage(from, "Don't be obnoxious, I'll answer up to " + this.jiraPlugin.getConfig().settings.repeatDelayMessages + " JIRA requests at a time.");
         return;
      }

      boolean isChannel = from.startsWith("#");
      
      // TODO: Make this configurable.
      boolean skipCache = msgText.contains("refresh") 
                       || msgText.contains("nocache")
                       || msgText.contains("resolved") 
                       || msgText.contains("closed") 
                       || msgText.contains("fixed") 
                       || msgText.contains("reopened") 
                       || msgText.contains("assigned");
      
      boolean noURL     = msgText.contains("nourl");

      // For each JIRA ID found...
      for( String issueID : jiraIDs )
      {
         this.handleIssueId( issueID, from, msgText, isChannel, skipCache, noURL, bot );
      }
   }// handleJiraRequest()




   /**
    * Hanles single issue ID encountered in a chat.
    * @param issueID
    * TODO: Sanitize params.
    */
   private void handleIssueId( String issueID, String from, String request,
           boolean isChannel, boolean skipCache, boolean noURL,
           IrcBotProxy bot
   ) {

        // TODO: Refactor - repeated in other method.
        String replyTo = this.jiraPlugin.getConfig().settings.debug ? this.jiraPlugin.getConfig().settings.debugChannel : from;

        // Skip Jiras with ignored prefixes.
        if( this.repoManager.hasIgnoredPrefix(issueID) )
            return;

        IssueInfo issue = null;

      

        // Show after at least 15 messages and 3 minutes.
        // TODO: Make this configurable.
        IssueShownInfo repeatedShowThreshold = new IssueShownInfo( 
                 this.jiraPlugin.getConfig().settings.repeatDelayMessages, 
                 this.jiraPlugin.getConfig().settings.repeatDelaySeconds * 1000 );
        long curTimeMS = System.currentTimeMillis();

        // Check the channel activity - when this issue was last shown.
        if( isChannel && this.channelStatusStore.isTooEarlyToRepeat( from, issueID, curTimeMS, repeatedShowThreshold ) )
            return;

        // Update the channel info.
        if( isChannel )
            this.channelStatusStore.onIssueAnnounced( from, issueID );


        // Check the cache.
        issue = this.issueCache.getItem( issueID );
        log.debug("  Cached issue info: " + issue);

        if( issue == null ){
            RepositoryBean repo = this.repoManager.getRepoForIssue( issueID );
            log.debug("Repo for this issue: " + repo);
            if( null == repo ) return;

            try {
                
                // === Get the JIRA info from Scraper. ===
                issue = repo.scrapeIssueInfo( issueID );
               
                // No issue info and no exception - should not happen.
                if( issue == null ){
                   log.warn("Issue scraper of "+repo.getName()+" should not return null! Issue: "+issueID);
                   return;
                }
            }
            catch( ScrapingException ex ){
               log.warn( ex.toString() );
               bot.sendMessage( replyTo, ex.getMessage() );
               return;
            }
        }

        // If the request contains the URL, don't repeat it.
        String urlPart = StringUtils.substringAfter( issue.getUrl(), "//");
        boolean noURLcur = noURL || request.contains( urlPart );

        // Create and send the message.
        String reply = String.format( noURLcur ? "%s" : "%s %s",
            this.formatResponse( issue ), issue.getUrl() );
        bot.sendMessage( replyTo, reply );

        // Update the cache.
        this.issueCache.putItem( issueID, issue );

    }// handleIssueId()



    /**
     * Formats the string for response for the given issue.
     * @returns  "[#TOOLS-102] Implement Bugzilla support [Open, Major, Ondrej Zizka]
     */
    public String formatResponse(IssueInfo issue) {
            String reply = String.format("[#%s] %s [%s, %s, %s]",
                                            issue.getId(),
                                            issue.getTitle(),
                                            issue.getStatus(), issue.getPriority(), issue.getAssignedTo());
            return reply;
    }


    /**
     * Handles a command, which is (assumably) sent as PM to the bot.
     *
     * @param replyTo     Nick or channel (#...) from which the message was received.
     * @param command  The command - the relevant part - i.e. ignoring
     *                 "jirabot" etc at the beginning of the messagge.
     *
     * @returns  true if the request was valid JiraBot command.
     */
    boolean handleJiraBotCommand( String from, String command, boolean isFromPrivateMessage, IrcBotProxy bot ) {

        boolean wasValidCommand = false;

        String replyTo = this.jiraPlugin.getConfig().settings.debug ? this.jiraPlugin.getConfig().settings.debugChannel : from;

        command = command.toLowerCase();

        // Clear cache.
        if (command.startsWith("clearcache")) {
            wasValidCommand = true;
            String clearJiraID = command.substring(10).trim().toUpperCase();
            if ("".equals(clearJiraID)) {
                bot.sendMessage(replyTo, "Clearing the cache.");
                this.issueCache.clear();
            } else {
                bot.sendMessage(replyTo, "Removing " + clearJiraID + " from the cache.");
                this.issueCache.removeItem(clearJiraID);
            }
        }

        return wasValidCommand;

    }// handleJiraBotCommand()


}// class

