
package org.jboss.jawabot.irc;

import cz.dynawest.util.plugin.PluginUtils;
import cz.dynawest.util.plugin.cdi.CdiPluginUtils;
import org.jboss.jawabot.ex.UnknownResourceException;
import org.jboss.jawabot.ex.JawaBotIOException;
import org.jboss.jawabot.ex.JawaBotException;
import java.util.*;
import java.util.Map.Entry;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.jboss.jawabot.config.beans.ConfigBean;
import org.jboss.jawabot.state.beans.StateBean;
import org.jibble.pircbot.PircBot;
import org.apache.log4j.Logger;
import org.jboss.jawabot.JawaBot;
import org.jboss.jawabot.JawaBotUtils;
import org.jboss.jawabot.MailData;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.ReservationCalendar;
import org.jboss.jawabot.ReservationWrap;
import org.jboss.jawabot.Resource;
import org.jboss.jawabot.ResourceManager.ReservationsBookingResult;
import org.jboss.jawabot.config.beans.ServerBean;
import org.jboss.jawabot.irc.model.IrcMessage;
import org.jibble.pircbot.NickAlreadyInUseException;



/**
 * JawaBot IRC module implementation.
 *
 * @author Ondrej Zizka
 */
@Dependent
public class JawaIrcBot extends PircBot
{
   private static final Logger log = Logger.getLogger( JawaIrcBot.class );

   
	final String USUAL_NICK = "jawabot";

   
   private JawaBot jawaBot;
   public JawaBot getJawaBot() { return jawaBot; }
   public void setJawaBot(JawaBot jawaBot) { this.jawaBot = jawaBot; }
      
   private boolean initialized = false;
   public boolean isInitialized() {      return initialized;   }

   // Plugin instances "placeholder".
   // TODO:  Change somehow to Weld.instance() or such and move to the method.
   @Inject private Instance<IIrcPluginHook> pluginHookInstances;

   /** Plugins map */
   private SortedMap<String, IIrcPluginHook> pluginsByClass = new TreeMap();
   
   /** Plugins list */
   private List<IIrcPluginHook> plugins;
   
   //private ConfigBean config;
   public ConfigBean getConfig() {      return this.getJawaBot().getConfig();   }

   private CommandHandler commandHandler;

   
   
   
   /** Const. */
   public JawaIrcBot() {  }
   
   public JawaIrcBot( JawaBot jawaBot ) {
      this.jawaBot = jawaBot;
   }
   
   
   

   /** Returns a list of all reservation calendars. */
   Map<Resource, ReservationCalendar> getReservationCalendars(){
      return this.getJawaBot().getResourceManager().getReservationCalendars();
   }
   void setReservationCalendars( Map<Resource, ReservationCalendar> cals ){
      this.getJawaBot().getResourceManager().setReservationCalendars( cals );
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
    *  Iterate available plugins, and initialize them.
    */
   private void initPlugins() {
      /*
      try {
         String[] pluginNames = new String[] {
            "org.jboss.jawabot.irc.plugin.logger.LoggerIrcPluginHook",
            //"org.jboss.jawabot.irc.plugin.resources.ResourcesIrcPluginHook", // Former JawaBot
         };
         List<IIrcPluginHook> plugins = PluginUtils.initAndStartModules( pluginNames, IIrcPluginHook.class,  getJawaBot() );
         for( IIrcPluginHook plugin : plugins ) {
            this.pluginsByClass.put( plugin.getClass().getName(), plugin );
         }
      }
      catch( PluginEx ex ){
         throw new JawaBotException("Failed loading IRC plugins: " + ex.getMessage(), ex);
      }
       */
   }
   
   /**
    *  Initialization of all plugins.
    *  Copied from JawaBotApp. TODO: Generalize? Or should I rely on CDI?
    * @deprecated  in favor of CdiPluginUtils.initAndStartPlugins()
    */
   private void initAndStartPlugins() throws JawaBotException {

      
      // For listing of init errors.
      List<Throwable> exs = new ArrayList<Throwable>();
      List<String> errModules = new ArrayList<String>();
      
      //IModuleHook[] moduleHooks = new IModuleHook[moduleHookInstances];
      List<IIrcPluginHook> pluginHooks = new ArrayList();
      for( IIrcPluginHook hook : this.pluginHookInstances ) {
         pluginHooks.add(hook);
      }
      
      // Init
      for( IIrcPluginHook hook : pluginHooks ) {
         try {
            if( null == hook ) continue;
            hook.initModule( getJawaBot() );
         } catch( Throwable ex ) {
            exs.add( ex );
            errModules.add( hook.getClass().getName() );
         }
      }
      
      // Start
      for( IIrcPluginHook hook : pluginHooks ) {
         try {
            if( null == hook ) continue;
            hook.startModule();
         } catch( Throwable ex ) {
            exs.add( ex );
            errModules.add( hook.getClass().getName() );
         }
      }
      
      PluginUtils.throwFormattedExceptionIfNeeded( exs, errModules, JawaBotException.class );
      
   }// initAndStartPlugins()
   

   


   /**
    * Connects the server(s), joins the channels...
    */
   public void connectAndJoin() throws JawaBotException {
      log.info("Connecting...");
      if( this.isConnected() )
         log.error("Already connected.");

      ConfigBean cnf = this.getConfig();

      /* PircBot only supports one server connection.
      // Connect to all servers.
      List<Exception> exs = new ArrayList();
      for( ServerBean server : cnf.irc.servers ){
         try{
            // Connect to the server
            this.connect( server.host ); // Red Hat

            // Join the default channels
            for( String channel : server.autoJoinChannels ){
               this.joinChannel(channel); // debugging
            }
         }
         catch( Exception ex ){
            String msg = "Exception when connecting to the server "+server.host+": "+ex;
            log.error( msg );
            exs.add( new JawaBotException(msg, ex));
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

      // Connect to the server
      nickTry: try{
         for( int i = 1; i <= 5; i++ ){  // 5 - max Nick tries.
            log.info("Trying nick '"+nickToTry+"'...");
            try {
               this.setName( nickToTry  );
               this.connect( server.host );
               break nickTry;
            }catch (NickAlreadyInUseException ex){
               log.warn("Nick already in use.");
               nickToTry = cnf.irc.defaultNick + "-" + i;
            }
         }
      }
      catch( Exception ex ){
         String msg = "Exception when connecting to the server "+server.host+": "+ex;
         throw new JawaBotException(msg, ex);
      }


      log.info("Joining channels...");

      // Join the default channels
      for( String channel : server.autoJoinChannels ){
         log.info(" * joining '"+channel+"'");
         this.joinChannel(channel);
      }
      this.joinChannel("#some");

      log.info("Connecting done.");
      
   }// connectAndJoin()






	/**
	 * Callback to handle a channel message:
    * Checks whether the msg starts with bot's nickname, and if so,
    * calls handleJawaBotCommand(), giving the channel name,
    * calling user's nick and the message.
	 */
	@Override
	protected void onMessage(String channel, String sender, String login, String hostname, String msgText)
   {
      if( ! this.isInitialized() ){
         log.warn("Called onMessage(), but not initialized yet.");
         return;
      }


      // Either process a command or search for Jira IDs.
      boolean wasCommand = false;

      String msgNorm = msgText.trim().toLowerCase();

      // Check for presence of bot nick prolog.
      boolean startsWithUsualNick = IrcUtils.isMsgForNick( msgNorm, USUAL_NICK );
      boolean startsWithBotNick = IrcUtils.isMsgForNick( msgNorm, this.getNick() );

      // If the prolog is present,
      if( startsWithUsualNick || startsWithBotNick ){

         // remove it,
         int prologEnd;
         if( startsWithUsualNick && startsWithBotNick ){
            prologEnd = Math.max( USUAL_NICK.length(), this.getNick().length() );
         }
         else {
            prologEnd = startsWithUsualNick ? USUAL_NICK.length() : this.getNick().length();
         }

         // Get the rest of the message sans eventual starting colon.
         String command = msgNorm.substring( prologEnd );
         command = StringUtils.removeStart(command, ":").trim();
         command = StringUtils.removeStart(command, ",").trim();

         // and process the command.
         wasCommand = handleJawaBotCommand(channel, sender, command);

      }


      // Not a command?
      if( !wasCommand ){
         
         IrcMessage msg = new IrcMessage("not.supported.yet", sender, channel, msgText, new Date());
         
         // Pass it to the IRC plugins.
         //for ( Entry<String, IIrcPluginHook> entry : this.pluginsByClass.entrySet() ) {
         for( IIrcPluginHook plugin : this.plugins ) {
            try {
               plugin.onMessage( msg );
            }
            // TODO: Filter repeated exceptions.
            //catch( IrcPluginException ex ) {
            catch( NullPointerException ex ) {
               log.error( "Plugin misbehaved: " + ex, ex );
            }
            catch( Throwable ex ) {
               if( System.getProperty("bot.irc.plugins.noStackTraces") == null )
                  log.error( "Plugin misbehaved: " + ex.getMessage(), ex );
               else {
                  log.error( "Plugin misbehaved: " + ex );
                  if( ex.getCause() != null )
                     log.error( "  Cause: " + ex.getCause() );
               }
            }
         }// for
         
      }// if( !wasCommand )

   }// onMessage()



   /** Private IRC message - no channel, only from user. */
	@Override
	protected void onPrivateMessage(String sender, String login, String hostname, String message) {
      handleJawaBotCommand(null, sender, message.trim());
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
	 */
	private boolean handleJawaBotCommand( String fromChannel, String fromUser, final String commandOrig )
   {
      log.debug( String.format("%s %s %s", fromChannel, fromUser, commandOrig) );


      // Command context. TODO: Make use of it in all commands.
      CommandContext ctx = new CommandContext(fromUser, fromChannel);

      // Command handler reply.
      CommandReply reply = null; // TBD: Temporarily being set to null, until all commands are moved.
      

		boolean wasValidCommand = false;
		boolean wasValidSyntax  = true;
		boolean stateChanged = false; // TODO: Watch state changes in the reservationsManager.

		// Is private message?
		boolean isFromPrivateMessage = null == fromChannel;

		// Reply either to PM or to a channel.
		String replyTo = isFromPrivateMessage ? fromUser : fromChannel;
      //System.out.println("isFromPrivateMessage: "+isFromPrivateMessage);///

		String command = commandOrig.toLowerCase();

      
		// Take / Keep
		if( command.startsWith("take") || command.startsWith("keep") ) {
			wasValidCommand = true;

         String params = command.substring(4).trim();
         reply = commandHandler.onTake( ctx, params );
         
		}// take



		// Find
		else if( command.startsWith("find") || command.startsWith("free") ) {
			wasValidCommand = true;

			String params = commandOrig.substring(4).trim();
         reply = this.commandHandler.onFind( ctx, params );
         
		}



		// Leave (resource)
		else if( command.startsWith("leave") ) cmd_leave: {
			wasValidCommand = true;

			String params = command.substring(5).trim();
         reply = commandHandler.onLeave( ctx, params );

		}// cmd_leave


      
		// List
		else if( command.startsWith("list") ) {
			wasValidCommand = true;
         
         String params = command.substring(4).trim();
         reply = commandHandler.onList( ctx, params );
		}



      // Save the state. Non-private to "prevent" someone flooding.
		/*else if( !isFromPrivateMessage && command.startsWith("save") ) {
			wasValidCommand = true;
         try {
            this.getJawaBot().saveState();
   			sendMessage(replyTo, "State saved.");
         } catch( JawaBotIOException ex ) {
            String msg = "Error saving state: "+ex.getMessage();
            sendMessage(replyTo, msg);
            log.error( msg, ex);
         }
		}*/



		// Join a channel.
		else if( command.startsWith("join") ) {
			wasValidCommand = true;
         reply = commandHandler.onJoin( ctx, command.substring(4).trim() );
		}


		// Leave the current channel.
		else if( !isFromPrivateMessage && command.startsWith("please leave") ) {
			wasValidCommand = true;
			sendMessage(replyTo, "Bye everyone. I'll be around; if you miss me later, /invite me.");
			this.partChannel(fromChannel, "Persona non grata.");
		}


		// Die - PM only.
		else if (  ( isFromPrivateMessage && command.startsWith("quit " + this.getJawaBot().getQuitPassword()) )
						 ||
                 ( this.getJawaBot().getConfig().settings.unsecuredShutdown && command.startsWith("diedie") )
              )
		{
			wasValidCommand = true;
         stateChanged = true;
			sendMessage(replyTo, "Bye, shutting down.");
			//this.partChannel(from, "Warp core overload."); // From private message.
			this.disconnect();
		}

      
		// About or Help.
		else if( command.startsWith("about") || command.startsWith("help") ) {
			wasValidCommand = true;
         reply = commandHandler.onHelp( ctx, command );
		}



      log.debug("Done processing command. Reply is: "+reply);

      
      // Temporarily in an if; after moving, throw if null.
      if( reply != null ){
         // Temporary - copy the flag.
         stateChanged = reply.stateChanged;
         wasValidSyntax = ! reply.reportInvalidSyntax;

         // Send IRC messages.

         // Default and debug channel.
         reply.additionalAnnounceChannels.add( this.getConfig().settings.announceDefaultChannel );
         reply.additionalAnnounceChannels.add( this.getConfig().settings.debugChannel );

         boolean noDangerOfReplyDubbing = ctx.isPrivate || ! reply.additionalAnnounceChannels.contains( replyTo );

         // Send IRC messages.
         for( CommandReplyMessage msg : reply.ircMessages ){
            if( msg.isAnnouncement )
            for( String sendTo : reply.additionalAnnounceChannels ){
               sendMessage( sendTo, msg.text );
            }
            // Prevent duplication: To send this reply, it must not go to the channel where it already was sent to.
            if( msg.isReply && ( noDangerOfReplyDubbing || ! msg.isAnnouncement ) ){
               sendMessage( replyTo, msg.text );
            }
         }

         // Send mail announcements.
         for( MailData mail : reply.mailAnnouncements ){
            trySendMail( mail, ctx.fromUserNorm, ctx.fromChannel );
         }
      }
      

      // If the state changed, save.
      // TODO: Watch state changes in the reservationsManager.
      if( stateChanged ){
         try {
            this.getJawaBot().saveState();
            String msg = String.format("State saved after %s on %s did command: %s", fromUser, fromChannel, commandOrig );
            log.info( msg );
   			sendDebugMessage( msg );
         } catch (JawaBotIOException ex) {
            String msg = "Error saving state: "+ex.getMessage();
            log.error( msg, ex );
            sendDebugMessage( msg );
         }
      }


      // Invalid command?
      if( ! wasValidCommand ){
         sendMessage( replyTo, "Invalid command, see " + JawaBotApp.PROJECT_DOC_URL );
      }

      // Invalid syntax?
      if( ! wasValidSyntax ){
         sendMessage( replyTo, "Invalid command syntax, see " + JawaBotApp.PROJECT_DOC_URL );
      }

		return wasValidCommand;

	}// handleJiraBotCommand()






   
   
	/**
    * Sends an announcement mail to a mailing list (currently jboss-qa-brno).
    * @deprecated in favor of #announceTakeOnMailingList( ReservationsBookingResult bookingResult, String customComment)
	 */
	private void announceTakeOnMailingList( Resource resource, ReservationWrap reservation, String customComment )
   {
      ConfigBean cnf = this.getConfig();
      String subject = JawaBotUtils.formatReservationInfoLine(resource.getName(), reservation);
      trySendMail( new MailData( subject, customComment ), reservation.getForUser(), cnf.settings.announceDefaultChannel);
	}



	/**
    * Sends an announcement mail to a mailing list.
    * @deprecated  in favor of CommandHandlerImpl.createTakeAnnouncementMail() .
	 */
   private void announceTakeOnMailingList( ReservationsBookingResult bookingResult, String customComment )
   {
      ConfigBean cnf = this.getConfig();
      String subject = JawaBotUtils.formatReservationInfoLine( bookingResult );

      // Message body.

      // Custom comment.
      StringBuilder sb = new StringBuilder();
      if( null != customComment )
         sb.append(customComment).append("\n");

      // List the reservations.
      if( bookingResult.resultingReservations.size() > 1 ){
         for( ReservationWrap resvWrap : bookingResult.resultingReservations ){
            sb.append( JawaBotUtils.formatReservationInfoLine(resvWrap.resourceName, resvWrap) );
            sb.append("\n");
         }
      }

      String messageBody = sb.toString();
      
      trySendMail( new MailData( subject, messageBody ), bookingResult.claimedResv.getForUser(), cnf.settings.announceDefaultChannel );
   }



   /**
    * Tries to send a mail; eventual failure is announced on the given channel.
    */
   private void trySendMail( MailData mail, String fromUser, String fallbackErrorMsgChannel ){
      try{
         // Send the mail announcement.
         sendMail( fromUser, mail );
      }
      catch (JawaBotException ex){
         String excMessage = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
         String reply = excMessage; //"Unable to send announcement email: "+excMessage;
         log.error(reply);
         sendMessage(fallbackErrorMsgChannel, reply);
      }
   }


   /**
    * Sends a mail announcement about user's action.
    */
   private void sendMail( String fromUser, MailData mail ) throws JawaBotException {

      ConfigBean cnf = this.getConfig();

      log.debug( String.format("Sending mail: host %s, to %s, from %s <%s>",
			cnf.settings.smtpHost,
			cnf.settings.announceEmailTo,
         fromUser,
			cnf.settings.announceEmailFrom
      ) );

      String messageBody = ( StringUtils.isBlank(mail.messageBody) ? "" : mail.messageBody + "\n\n");
      messageBody += "Possible sender's e-mail: "+fromUser+"@redhat.com\nThis message was "
              + "generated by JawaBot "+JawaBotApp.VERSION+".\n" + JawaBotApp.PROJECT_DOC_URL;
      mail.messageBody = messageBody;
      mail.fromName = fromUser+" via JawaBot";

      this.jawaBot.getMailUtils().sendMail( mail );

   }










	@Override
	protected void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel) {
      //if( this.getConfig().getSettingBool(SETID_ACCEPT_INVITATION))
			this.joinChannel(channel);
	}


   /*
	@Override
	protected void onPart(String channel, String sender, String login, String hostname) {
			System.out.println("Channels: " + this.getChannels().length  + " - " + Arrays.toString(this.getChannels()));

			// Quit if no channels left.
			if( this.getChannels().length == 0 ) {
					this.disconnect();
			}
	}/**/



	@Override
	protected void onDisconnect() {
      log.info("onDisconnect().");
		// TODO: Reconnect on disconnect - to get over network outages.
		this.dispose();
      synchronized(this){
         this.notifyAll();
      }
	}



   /**
    * Configures this bot according to the given config bean (possibly read from XML).
    * @param config
    */
   public void applyConfig( ConfigBean config )
   {
      //this.config = config;

      // Settings.
      this.setVerbose( config.settings.verbose ); // Enable debugging output.
      this.setMessageDelay( config.settings.messageDelay );

   }

   

   /**
    * Extracts configuration data into a ConfigBean to be persisted.
    * @return
    */
   public ConfigBean extractConfig() {
      throw new UnsupportedOperationException("Not yet implemented");
   }





   /** Updates the bot to according to the state stored in the bean. */
   private void applyState( StateBean state ) throws UnknownResourceException {
      throw new UnsupportedOperationException("Not yet implemented");
   }


   
   /** Extracts the state bean from the bot's current state. */
   public StateBean extractState() {

      StateBean state = new StateBean();

      return state;
   }






   /** Send a message to the debug channel. */
   private void sendDebugMessage(String msg) {
      this.sendMessage( this.getConfig().settings.debugChannel, msg );
   }


}// class


