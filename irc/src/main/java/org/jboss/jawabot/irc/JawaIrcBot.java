package org.jboss.jawabot.irc;

import cz.dynawest.util.plugin.cdi.CdiPluginUtils;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import org.jboss.jawabot.ex.JawaBotException;
import java.util.*;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.jboss.jawabot.config.beans.ConfigBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.jawabot.JawaBot;
import org.jboss.jawabot.MailData;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.config.beans.ServerBean;
import org.jboss.jawabot.irc.ent.IrcEvAction;
import org.jboss.jawabot.irc.ent.IrcEvJoin;
import org.jboss.jawabot.irc.ent.IrcEvMessage;
import org.jboss.jawabot.irc.ent.IrcEvNickChange;
import org.jboss.jawabot.irc.ent.IrcEvPart;
import org.jibble.pircbot.IrcServerConnection;
import org.jibble.pircbot.beans.User;
import org.jibble.pircbot.ex.NickAlreadyInUseException;
import org.jibble.pircbot.handlers.IrcProtocolEventHandler;

/**
 *  JawaBot IRC module.
 * 
 *  @author Ondrej Zizka
 */
@Dependent
public class JawaIrcBot extends IrcProtocolEventHandler
{
    private static final Logger log = LoggerFactory.getLogger( JawaIrcBot.class );
    
   
    final String USUAL_NICK = "jawabot";


    // App's main object.
    private JawaBot jawaBot;
    public JawaBot getJawaBot() { return jawaBot; }
    void setJawaBot(JawaBot jawaBot) { this.jawaBot = jawaBot; }

    // IRC server connection. Formerly, this class extended PircBot.
    private IrcServerConnection conn;
    public IrcServerConnection getConn() { return conn; }
    
    
    // Proxy provided to plugins to allow them to send messagesetc.
    // Will likely be changed to our own API with objects like IrcMessage.
    private IrcBotProxy pircBotProxy;

    private volatile boolean initialized = false;
    private boolean isInitialized() { return initialized; }
    
    // Because Pircbot won't let us know whether we calleddisconnect() or it came from server.
    // Since we can't even override connect (damn PircBot), we'll have to set and re-set it for every disconnect()/connect().
    private boolean intentionalDisconnect = false;
    private boolean isIntentionalDisconnect() {      return intentionalDisconnect;   }
    
    // Plugin instances "placeholder".    // TODO:  Change somehow to Weld.instance() or such and move to the method.
    @Inject
    private Instance<IIrcPluginHook> pluginHookInstances;
    
    /** Plugins map */
    private SortedMap<String, IIrcPluginHook> pluginsByClass = new TreeMap();
    
    /** Plugins list */
    private List<IIrcPluginHook> plugins;

    //private ConfigBean config;
    public ConfigBean getConfig() { return this.getJawaBot().getConfig(); }
    
    private CommandHandler commandHandler;
    
    
    
    
    /**  Current handler of PircBot's onChannelInfo(). 
     *   PircBot callbacks are called synchronously, so we don't need synchronization.
     */
    private ChannelInfoHandler currentOnChannelInfoHandler = null;
    
    private void setCurrentChannelInfoHandler(ChannelInfoHandler handler) {
        this.currentOnChannelInfoHandler = handler;
    }

    
    /**  Current handler of PircBot's onUserList().  */
    private Map<String, UserListHandler> currentOnUserListHandlers = new HashMap();

    private UserListHandler setCurrentUserListHandler(String channnel, UserListHandler handler) {
        return this.currentOnUserListHandlers.put(channnel, handler);
    }

    
    
   
    /** Const. */
    public JawaIrcBot() {
        super();
        this.pircBotProxy = new IrcBotProxy(this);
    }

    /** Not used but should be - to keep initialization safe... how to, with CDI? */
    public JawaIrcBot(JawaBot jawaBot) {
        this();
        this.setJawaBot(jawaBot);
    }

   
   

    /**
     * Init - .
     */
    public synchronized void init() throws JawaBotException {
        log.info("Initializing...");
        if( this.initialized )
            log.warn("Already initialized.");


        // Create a command handler for core commands.
        this.commandHandler = new CommandHandlerImpl(this);

        //this.initAndStartPlugins();
        this.plugins = CdiPluginUtils.initAndStartPlugins( this.pluginHookInstances, JawaBotApp.getJawaBot(), JawaBotException.class );

        this.initialized = true;
    }

   
   
    /**
     *  Should connect() and wait().
     *  Will get notify() onDisconnect(), so it should recover.
     *  UNUSED, UNTESTED!
     */
    public void connectAndReconnectOnDisconnect() throws JawaBotException {
        while( true ){
            synchronized( this ){
                this.connectAndJoin();
                try {
                    this.wait();
                } catch (InterruptedException ex) {
                    log.warn("Interrupted in connectAndReconnectOnDisconnect(): " + ex.getMessage(), ex);
                }
                if( this.isIntentionalDisconnect() )
                    break;
            }
        }
    }



    /**
     * Connects the server(s), joins the channels...
     */
    public void connectAndJoin() throws JawaBotException {
        log.info("Connecting...");
        if( this.conn.isConnected() )
            log.error("Already connected.");

        ConfigBean cnf = this.getConfig();

        /* PircBot only supports one server connection.
        // Connect to all servers.
        List<Exception> exs = new ArrayList();
        for( ServerBean server : cnf.irc.servers ) {
            try {
                // Connect to the server
                this.connect( server.host ); // Red Hat

                // Join the default channels
                for( String channel : server.autoJoinChannels ) {
                    this.joinChannel( channel ); // debugging
                }
            } catch( Exception ex ) {
                String msg = "Exception when connecting to the server " + server.host + ": " + ex;
                log.error( msg );
                exs.add( new JawaBotException( msg, ex ) );
            }
        }

        // All connections failed.
        if( cnf.irc.servers.size() == exs.size() )
            throw new JawaBotException("Connecting to all servers failed, see previous messages.");
         */

        if( cnf.irc.servers.size() == 0 )
            throw new JawaBotException("No IRC servers configured.");

        ServerBean server = cnf.irc.servers.get(0);
        String nickToTry = cnf.irc.defaultNick;

        final int DELAY_SEC_ADD_IN_NEXT_ATTEMPT = 14;
        final int NICK_IN_USE_DELAY_SEC = 15;
        final int INITIAL_DELAY_SEC = 1;
        final int MAX_NICK_TRIES = 5;

        // Connect to the server
        nickTry: try {
            this.conn.setVerbose(true);
            int delaySec = INITIAL_DELAY_SEC;
            for( int i = 1; i <= MAX_NICK_TRIES; i++ ) {
                log.info("Trying nick '" + nickToTry + "'...");
                try {
                    this.conn.setName( nickToTry  );
                    this.intentionalDisconnect = false;
                    this.conn.connect(server.host);
                    // Wait for potential "ERROR :Trying to reconnect too fast."
                    log.info("Waiting " + delaySec + " seconds for potential \"ERROR :Trying to reconnect too fast.\"");
                    Thread.sleep( delaySec * 1000 );
                    delaySec += DELAY_SEC_ADD_IN_NEXT_ATTEMPT;

                    // On nick clash or when reconnecting too quickly, IRC server disconnects us.
                    if (this.conn.isConnected()) {
                        log.info("Connected to " + server.host);
                        break nickTry;
                    }
                } catch (NickAlreadyInUseException ex) {
                    log.warn("Nick already in use. Waiting few seconds. ");
                    Thread.sleep(NICK_IN_USE_DELAY_SEC * 1000);
                    nickToTry = cnf.irc.defaultNick + "-" + i;
                    //log.info("Changing nick to '"+nickToTry+"'...");
                    //this.changeNick(nickToTry);
                } catch (UnknownHostException ex) {
                    throw new JawaBotException("Unknown host: " + server.host);
                }
            }
            throw new JawaBotException("Could not find unique nick after " + 5 + " attempts, giving up.");
        } catch (Exception ex) {
            String msg = "Exception when connecting to server " + server.host + ": " + ex;
            throw new JawaBotException(msg, ex);
        }


        log.info("Joining channels..." + this.conn.isConnected());

        // Join the default channels.
        for (String channel : server.autoJoinChannels) {
            log.info(" * joining '" + channel + "'");
            this.conn.joinChannel(channel);
        }
        this.conn.joinChannel("#some");

        log.info("Connecting done.");
        assert this.conn.isConnected();

    }// connectAndJoin()

    
    
    
    
    
    /**
     * Callback to handle a channel message:
     * Checks whether the msg starts with bot's nickname, and if so,
     * calls handleJawaBotCommand(), giving the channel name,
     * calling user's nick and the message.
     */
    @Override
    public void onMessage( String channel, String sender, String login, String hostname, String msgText ) {
        if( ! this.isInitialized() ) {
            log.warn("Called onMessage(), but not initialized yet.");
            return;
        }


        // Either process a command or dispatch the message to plugins.
        boolean wasCommand = false;

        String msgNorm = msgText.trim().toLowerCase();

        // Check for presence of bot nick prolog.
        int prologEnd = Math.max(
              IrcUtils.getMsgStartAfterNick( msgNorm, USUAL_NICK ),
              IrcUtils.getMsgStartAfterNick( msgNorm, this.conn.getNick() ) );
        
        if( prologEnd != 0 ){

            // Get the rest of the message sans eventual starting colon.
            String command = msgNorm.substring( prologEnd );
            command = StringUtils.removeStart( command, ":").trim();
            command = StringUtils.removeStart( command, ",").trim();

            // and process the command.
            wasCommand = this.handleJawaBotCoreCommand( channel, sender, command );
        }


        // Not a command?
        if( ! wasCommand ) {

            IrcEvMessage msg = new IrcEvMessage( null, channel, sender, msgText, new Date() );

            // Pass it to the IRC plugins.
            //for ( Entry<String, IIrcPluginHook> entry : this.pluginsByClass.entrySet() ) {
            for( final IIrcPluginHook plugin : this.plugins ) {
                new ExceptionHandlerDecorator() {
                    public void doIt( IrcEvMessage msg, IrcBotProxy pircBotProxy ) throws Throwable {
                        plugin.onMessage( msg, pircBotProxy );
                    }
                }.handle( msg, this.pircBotProxy );

            }// for

        }// if( !wasCommand )

    }// onMessage()



    /**
     * Private IRC message - no channel, only from user.
     */
    @Override
    public void onPrivateMessage( String sender, String login, String hostname, String msgText ) {

        // If it's a core command, don't pass it to  plugins.
        if (handleJawaBotCoreCommand(null, sender, msgText.trim())) {
            return;
        }


        // Was not a command -> handle with plugins.

        IrcEvMessage msg = new IrcEvMessage("not.supported.yet", sender, null, msgText, new Date());

        for( final IIrcPluginHook plugin : this.plugins ) {
            new ExceptionHandlerDecorator() {

                public void doIt( IrcEvMessage msg, IrcBotProxy pircBotProxy ) throws Throwable {
                    plugin.onPrivateMessage( msg, pircBotProxy );
                }
            }.handle( msg, pircBotProxy );
        }// for

    }

   
    /**
     *  Decorator to save me handling exceptions everywhere.
     *  TODO: Filter repeated exceptions.
     */
    private static abstract class ExceptionHandlerDecorator {

        public void handle( /*IIrcPluginHook plugin,*/ IrcEvMessage msg, IrcBotProxy pircBotProxy ) {
            try {
                this.doIt( msg, pircBotProxy );
            }
            //catch( IrcPluginException ex ) {
            catch( NullPointerException ex ) {
                log.error( "Plugin misbehaved: " + ex, ex );
            }
            catch ( Throwable ex ) {
                if( System.getProperty("bot.irc.plugins.noStackTraces") == null ) {
                    log.error("Plugin misbehaved: " + ex.getMessage(), ex);
                } else {
                    log.error("Plugin misbehaved: " + ex);
                    if (ex.getCause() != null) {
                        log.error("  Cause: " + ex.getCause());
                    }
                }
            }
        }

        public abstract void doIt(IrcEvMessage msg, IrcBotProxy pircBotProxy) throws Throwable;
    }





    /**
     * Handles a command, which is (assumably) sent as PM to the bot.
     *
     * @param fromChannel  Channel (#...) from which the message was received. Can be null for private messages.
     * @param fromUser     Nick from which the message was received.
     * @param command  The command - the relevant part - i.e. ignoring
     *                 "jawabot" etc at the beginning of the messagge.
     *
     * @returns  true if the request was valid JiraBot command.
     * 
     *  TODO:  Move reservation stuff to a plugin.
     */
    private boolean handleJawaBotCoreCommand(String fromChannel, String fromUser, final String commandOrig) {
        log.debug(String.format("%s %s %s", fromChannel, fromUser, commandOrig));


        // Command context. TODO: Make use of it in all commands.
        CommandContext ctx = new CommandContext(fromUser, fromChannel);

        // Command handler reply.
        CommandReply reply = null; // TBD: Temporarily being set to null, until all commands are moved.


        boolean wasValidCommand = false;
        boolean wasValidSyntax = true;
        boolean stateChanged = false; // TODO: Watch state changes in the reservationsManager.

        // Is private message?
        boolean isFromPrivateMessage = null == fromChannel;

        // Reply either to PM or to a channel.
        String replyTo = isFromPrivateMessage ? fromUser : fromChannel;
        //System.out.println("isFromPrivateMessage: "+isFromPrivateMessage);///

        String command = commandOrig.toLowerCase();





        // Join a channel.
        if( command.startsWith( "join" ) ) {
            wasValidCommand = true;
            reply = commandHandler.onJoin( ctx, command.substring( 4 ).trim() );
        }


        // Leave the current channel.
        else if( !isFromPrivateMessage && command.startsWith( "please leave" ) ) {
            wasValidCommand = true;
            this.conn.sendMessage( replyTo, "Bye everyone. I'll be around; if you miss me later, /invite me." );
            this.conn.partChannel( fromChannel, "Persona non grata." );
        }

            
        // Die - PM only.
        else if( 
            ( isFromPrivateMessage  &&  command.startsWith( "quit " + this.getJawaBot().getQuitPassword() ))
            ||
            (this.getJawaBot().getConfig().settings.unsecuredShutdown && command.startsWith( "diedie" )) 
        ) {
            wasValidCommand = true;
            stateChanged = true;
            this.conn.sendMessage( replyTo, "Bye, shutting down." );
            //this.partChannel(from, "Warp core overload."); // From private message.
            this.conn.disconnect();
        }

      
        // Join a channel.
        if( command.startsWith( "rename " ) ) {
            wasValidCommand = true;
            this.conn.changeNick( command.substring(7).trim() );
        }


        // About or Help.
        else if( command.startsWith( "about" ) || command.startsWith( "help" ) ) {
            wasValidCommand = true;
            reply = commandHandler.onHelp( ctx, command );
        }



        log.debug("Done processing command. Reply is: " + reply);


        // Temporarily in an if; after moving, throw if null.
        if( reply != null ) {
            // Temporary - copy the flag.
            stateChanged = reply.stateChanged;
            wasValidSyntax = !reply.reportInvalidSyntax;

            // Send IRC messages.

            // Default and debug channel.
            reply.additionalAnnounceChannels.add( this.getConfig().settings.announceDefaultChannel );
            reply.additionalAnnounceChannels.add( this.getConfig().settings.debugChannel );

            boolean noDangerOfReplyDubbing = ctx.isPrivate || !reply.additionalAnnounceChannels.contains( replyTo );

            // Send IRC messages.
            for( CommandReplyMessage msg : reply.ircMessages ) {
                if( msg.isAnnouncement ) {
                    for( String sendTo : reply.additionalAnnounceChannels ) {
                        this.conn.sendMessage( sendTo, msg.text );
                    }
                }
                // Prevent duplication: To send this reply, it must not go to the channel where it already was sent to.
                if( msg.isReply && (noDangerOfReplyDubbing || !msg.isAnnouncement) ) {
                    this.conn.sendMessage( replyTo, msg.text );
                }
            }

            // Send mail announcements.
            for( MailData mail : reply.mailAnnouncements ) {
                trySendMail( mail, ctx.fromUserNorm, ctx.fromChannel );
            }
        }


        // If the state changed, save.
        if( stateChanged ) {
            // TODO
        }


        // Invalid command?
        if( !wasValidCommand ) {
            // Nothing - plugins must check it too.
        }

        // Invalid syntax?
        if( !wasValidSyntax ) {
            this.conn.sendMessage( replyTo, "Invalid command syntax, see " + JawaBotApp.PROJECT_DOC_URL );
        }

        return wasValidCommand;

    }// handleJiraBotCommand()






   
 

    /**
     * Tries to send a mail; eventual failure is announced on the given channel.
     * TODO: Move to some MailService.
     */
    private void trySendMail( MailData mail, String fromUser, String fallbackErrorMsgChannel ) {
        try {
            // Send the mail announcement.
            sendMail( fromUser, mail );
        }
        catch( JawaBotException ex ) {
            String excMessage = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            String reply = excMessage; //"Unable to send announcement email: "+excMessage;
            log.error( reply );
            this.conn.sendMessage( fallbackErrorMsgChannel, reply);
        }
    }


    /**
     * Sends a mail announcement about user's action.
     * TODO: Move to some MailService.
     */
    private void sendMail( String fromUser, MailData mail ) throws JawaBotException {

        ConfigBean cnf = this.getConfig();

        log.debug( String.format( "Sending mail: host %s, to %s, from %s <%s>",
            cnf.settings.smtpHost,
            cnf.settings.announceEmailTo,
            fromUser,
            cnf.settings.announceEmailFrom ) );

        String messageBody = (StringUtils.isBlank( mail.messageBody ) ? "" : mail.messageBody + "\n\n");
        messageBody += "Possible sender's e-mail: " + fromUser + "@redhat.com\nThis message was "
            + "generated by JawaBot " + JawaBotApp.VERSION + ".\n" + JawaBotApp.PROJECT_DOC_URL;
        mail.messageBody = messageBody;
        mail.fromName = fromUser + " via JawaBot";

        this.jawaBot.getMailUtils().sendMail( mail );
    }

   
   

    /**
     *  Server sent channel info, likely on a plugin's request, or after connect.
     *  Send it to the appropriate handler - likely given by plugin.
     */
    @Override
    public void onChannelInfo( String channel, int userCount, String topic ) {
      if( this.currentOnChannelInfoHandler == null )
         return;
      this.currentOnChannelInfoHandler.onChannelInfo( channel, userCount, topic );
    }

    public void listChannels( ChannelInfoHandler handler ) {
        if( this.currentOnChannelInfoHandler != null) {
            // The same handler.
            if (this.currentOnChannelInfoHandler.equals(handler))  return;

            // Warning is the most we can do.
            log.warn("Overwriting current ChannelInfoHandler."
                    + "\n    Old: " + this.currentOnChannelInfoHandler
                    + "\n    New: " + handler);
        }
        this.currentOnChannelInfoHandler = handler;

        // PircBot will call onChannelInfo() and we will redirect these calls to the handler.
        // Unfortunatelly, there's no way to recognize when it finished.
        this.conn.listChannels();
    }


    
    /**
     *  Queue of channels to part.
     */
    private final Queue<ChannelToPart> partChannels = new DelayQueue();

    
    /**
     *  Gets users in a channel. PircBot does it asynchronously, so must we.
     */
    protected void listUsersInChannel( String channel, UserListHandler handler )
    {
        User[] users = this.conn.getUsers(channel);
        if( users.length != 0 ){
            log.debug("  Already in channel: " + channel);
            handler.setDisconnectFlag( false );
            handler.onUserList(channel, users);
            return;
        }

        UserListHandler oldHandler = this.setCurrentUserListHandler( channel, handler );
        if( null != oldHandler ){
            if( ! oldHandler.equals( handler ) ){
                // Warning is the most we can do.
                log.warn("  Overwriting current ChannelInfoHandler." + "\n    Old: " + oldHandler + "\n    New: " + handler);
            }
        }

        // Before joining a new channel, try to PART the hanged ones.
        ChannelToPart channelToPart;
        while( null != (channelToPart = partChannels.poll()) ){
            log.debug( "   Parting temp channel from a part queue: " + channelToPart.getName() );
            this.conn.partChannel(channelToPart.getName(), "I'm still here?");
        }

        log.debug("  Temporarily joining channel: " + channel);
        handler.setDisconnectFlag( true );
        this.conn.joinChannel(channel);
        // PircBot will (hopefully) call onUserList() and we will redirect these calls to the handler.
        partChannels.add( new ChannelToPart(channel, 10*1000) );
    }

    /**
     *  Server sent channel info, likely on a plugin's request, or after connect.
     *  Send it to the appropriate handler - likely given by plugin.
     */
    @Override
    public void onUserList( String channel, User[] users ) {
        UserListHandler handler = this.currentOnUserListHandlers.remove(channel);
        if( null == handler ){
            log.debug("  No onUserList() handler for channel: " + channel);
            return;
        }

        handler.onUserList( channel, users );
        if( handler.isDisconnectFlag() ){
            log.debug("  Parting temporarily joined channel: " + channel);
            this.conn.partChannel(channel);
        }
    }


    
    // Action
    @Override
    public void onAction( String sender, String login, String hostname, String target, String action ) {
        for( final IIrcPluginHook plugin : this.plugins ) {
            plugin.onAction( new IrcEvAction( null, target, sender, action,  new Date() ), this.pircBotProxy );
        }
    }

    
    // Someone joined a channel we're in.
    @Override
    public void onJoin( String channel, String sender, String login, String hostname ) {
        for( final IIrcPluginHook plugin : this.plugins ) {
            plugin.onJoin( new IrcEvJoin( null, channel, sender, login, hostname ), this.pircBotProxy );
        }
    }


    //  :JawaBot-debug!~PircBot@vpn1-6-95.ams2.redhat.com PART #frien
    @Override
    public void onPart( String channel, String sender, String login, String hostname ) {
        if( sender.equals( this.conn.getNick() ) )
            this.onPartUs( channel );
        else {
            Date now = new Date();
            for( final IIrcPluginHook plugin : this.plugins ) {
                plugin.onPart( new IrcEvPart( null, channel, sender, login+"@"+hostname, now ), this.pircBotProxy );
            }
        }
    }

    

    /**
     *  PircBot has common method for all parts, even our own. This fixes that.
     */
    private void onPartUs( String channel ) {
    }

    @Override
    public void onNickChange( String oldNick, String login, String hostname, String newNick ) {
        Date now = new Date();
        for( final IIrcPluginHook plugin : this.plugins ) {
            plugin.onNickChange( new IrcEvNickChange( null, oldNick, newNick, login, hostname, now ), this.pircBotProxy );
        }
    }

    @Override
    public void onQuit( String sourceNick, String sourceLogin, String sourceHostname, String reason ) {
        // TODO
    }


    @Override
    public void onInvite( String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel ) {
        //if( this.getConfig().getSettingBool(SETID_ACCEPT_INVITATION))
        this.conn.joinChannel( channel );
    }

    
    
    @Override
    public void onConnect() {
        for( final IIrcPluginHook plugin : this.plugins ) {
            plugin.onConnect( this.pircBotProxy );
        }
    }



    @Override
    public void onDisconnect() {
        log.info( "onDisconnect()." );

        for( final IIrcPluginHook plugin : this.plugins ) {
            plugin.onDisconnect( this.pircBotProxy );
        }

        // TODO: Reconnect on unintentional disconnect - to get over network outages.
        //       See connectAndReconnectOnDisconnect().      
        if( this.isIntentionalDisconnect() ) {
            log.info( "  Intentional disconnect, disposing PircBot." );
            this.conn.dispose();
        }
        synchronized( this ) {
            log.info( "  notifyAll() on PircBot@" + this.hashCode() );
            this.notifyAll();
        }
    }



    /**
     * Configures this bot according to the given config bean (possibly read from XML).
     * @param config
     */
    public void applyConfig( ConfigBean config ) {
        //this.config = config;

        // Settings.
        this.conn.setVerbose( config.settings.verbose ); // Enable debugging output.
        this.conn.setMessageDelay( config.settings.messageDelay );

    }



    /**
     * Extracts configuration data into a ConfigBean to be persisted.
     * @return
     */
    public ConfigBean extractConfig() {
        throw new UnsupportedOperationException("Not yet implemented");
    }






    /** Send a message to the debug channel. */
    public void sendDebugMessage( String msg ) {
        this.conn.sendMessage( this.getConfig().settings.debugChannel, msg );
    }


}// class
/**
 *  Info about channel we should part.
 *  This is needed because PART is not always really done, and the bot stays connected.
 * 
 *  TODO: Maybe better would be to take a snapshot of joined channels at the beginning and compare against it.
 */
class ChannelToPart implements Delayed {

    private String name;
    public String getName() { return name; }
    
    private long expireAt;
    public long getExpireAt() { return expireAt; }
    
    public ChannelToPart( String name, long delay ) {
        this.name = name;
        this.expireAt = new Date().getTime() + delay;
    }

    private long getRemaining() {
        return this.expireAt - new Date().getTime();
    }

        
    
    public long getDelay( TimeUnit unit ) {
        switch( unit ){
            case MILLISECONDS: return getRemaining();
            case MICROSECONDS: return getRemaining() * 1000L;
            default: return 0;
        }
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.getExpireAt() - ((ChannelToPart) o).getExpireAt());
    }
    
}
