package org.jboss.jawabot;

import java.io.File;
import java.io.IOException;
import org.jboss.jawabot.ex.UnknownResourceException;
import org.jboss.jawabot.ex.JawaBotIOException;
import org.jboss.jawabot.ex.JawaBotException;
import java.util.*;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.jboss.jawabot.config.beans.ConfigBean;
import org.jboss.jawabot.state.beans.StateBean;
import org.slf4j.Logger; import org.slf4j.LoggerFactory; 
import org.jboss.jawabot.ResourceManager.ReservationsBookingResult;
import org.jboss.jawabot.groupmgr.GroupManager;
import org.jboss.jawabot.plugin.pastebin.IPasteBinManager;
import org.jboss.jawabot.plugin.pastebin.JpaPasteBinManager;
import org.jboss.jawabot.state.JaxbStatePersister;
import org.jboss.jawabot.state.beans.ReservationBean;



/**
 * JawaBot implementation.
 *
 * TODO: Move actions from handleCommand() to some CommandHandlerImpl?
 * 
 * Created by splitting JavaIrcBot.java - separating IRC "front-end" stuff and backend stuff.
 * 
 * 
 * @author Ondrej Zizka
 */
@ApplicationScoped
public class JawaBot
{
   private static final Logger log = LoggerFactory.getLogger( JawaBot.class );

   

   private ConfigBean config;
   public ConfigBean getConfig() {      return config;   }

   private final MailUtils mailUtils = new MailUtils(this);
   public MailUtils getMailUtils() { return this.mailUtils; }

   private boolean initialized = false;
   public boolean isInitialized() {      return initialized;   }
   
	private int quitPassword = new Random().nextInt(1000);
   public int getQuitPassword() { return quitPassword; }
   
   


   // ResourceManager
	private ResourceManager resourceManager = new ResourceManager();
   @Produces @FromJawaBot
   public ResourceManager getResourceManager() {      return resourceManager;   }
   public void setResourceManager(ResourceManager resourceManager) { this.resourceManager = resourceManager; }

   
   // GroupManager
	private GroupManager groupManager = new GroupManager();
   public GroupManager getGroupManager() { return groupManager; }
   public void setGroupManager(GroupManager groupManager) { this.groupManager = groupManager; }
      

   // PasteBinManager
   //private MemoryPasteBinManager pasteBinManager = new MemoryPasteBinManager();
   @Inject private JpaPasteBinManager pasteBinManager;
   public IPasteBinManager getPasteBinManager() { return pasteBinManager; }
   
   


   /** [JAXB] Returns a list of all reservation calendars. */
   // Moved to here to omit one XML element level.
   //@XmlElement
   //@XmlJavaTypeAdapter( value = ReservCalendarMapAdaptor.class )
   Map<Resource, ReservationCalendar> getReservationCalendars(){
      return this.getResourceManager().getReservationCalendars();
   }
   void setReservationCalendars( Map<Resource, ReservationCalendar> cals ){
      this.getResourceManager().setReservationCalendars( cals );
   }


   
   

   /**
    * Creates the bot, loads the configuration, initializes and returns the bot.
    */
   public static JawaBot create( ConfigBean cb ) throws JawaBotException{
      JawaBot bot = new JawaBot();
      bot.applyConfig(cb);
      bot.init();
      return bot;
   }
	

	/** Const */
	public JawaBot() {
      // Log the quit password.
      log.info("");
      log.info("                        ***  QUIT PASSWORD: "+this.quitPassword+"  ***");
      log.info("");
	}



	/**
    * Init - loads the state (resources reservations) and stores the quit password.
    */
	public synchronized void init() throws JawaBotIOException, UnknownResourceException {
      log.info("Initializing...");
      if( this.initialized )
         log.warn("Already initialized.");

      String statePath = System.getProperty("jawabot.stateFile.path"); // Can be null
      StateBean state = new JaxbStatePersister().setFilePath(statePath).load();
      this.applyState( state );

      
      // Store the quit password.
      try {
         File passFile = new File("quit" + this.quitPassword + ".txt");
         FileUtils.touch( passFile );
         FileUtils.forceDeleteOnExit( passFile );
      } catch (IOException ex) {
         log.error("Can't write password file: "+ex, ex);
      }
     
      this.initialized = true;
	}









   
   
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
      catch( JawaBotException ex ){
         String excMessage = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
         String reply = excMessage; //"Unable to send announcement email: "+excMessage;
         log.error(reply);
         ///sendMessage( fallbackErrorMsgChannel, reply ); // TODO - rethrow?
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

		try {
			SimpleEmail email = new SimpleEmail();
			email.setHostName( cnf.settings.smtpHost );
			email.addTo( cnf.settings.announceEmailTo ); // TODO: Let it depend on the resource's group.
			email.setFrom( cnf.settings.announceEmailFrom, fromUser+" via JawaBot");
			email.setSubject( mail.subject );
         String messageBody = ( StringUtils.isBlank(mail.messageBody) ? "" : mail.messageBody + "\n\n");
			email.setMsg(messageBody + "Possible sender's e-mail: "+fromUser+"@redhat.com\nThis message was "
                 + "generated by JawaBot "+JawaBotApp.VERSION+".\n" + JawaBotApp.PROJECT_DOC_URL);
			email.send();
		} catch( EmailException ex ) {
			throw new JawaBotException("Can't mail to "+cnf.settings.announceEmailTo+": "+ex.getMessage(), ex);
		}
   }










	/**
	 * Clean up old reservations.
	 */
	private void doCleanUp(){
		log.info("Cleaning up.");
		this.getResourceManager().purgeOldReservations();
	}




   /**
    * Configures this bot according to the given config bean (possibly read from XML).
    * @param config
    */
   public void applyConfig( ConfigBean config )
   {
      this.config = config;

      // Resources.
      for( Resource res : config.getResources() ){
         //this.getResourceManager().addResource( new Resource( res.getName(), res.getProject() ));
         this.getResourceManager().addResource( res );
      }

      // TODO: Resource groups.

      // TODO: User groups.

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

      //List<UnknownResourceException> exs = new ArrayList();
      List<String> resNotFound = new ArrayList();

      // Apply reservations.
      this.getResourceManager().clearReservations();
      for( ReservationBean resv : state.reservations ){
         for( String resName : resv.resources ){
            try {
               this.getResourceManager().bookResources(resName, resv.forUser, resv.from, resv.to);
            } catch (UnknownResourceException ex) {
               resNotFound.add(resName);
            }
         }
      }

      if( resNotFound.size() != 0 )
         throw new UnknownResourceException("Some resources referenced in the persisted state were not found in configuration: "+StringUtils.join( resNotFound, ", ") );
      
   }


   
   /** Extracts the state bean from the bot's current state. */
   public StateBean extractState() {

      StateBean state = new StateBean();

      // For each resource...
      for( Map.Entry<Resource, ReservationCalendar> en : this.getResourceManager().getReservationCalendars().entrySet() ){
         Resource res = en.getKey();
         ReservationCalendar cal = en.getValue();

         List<String> resourceAsList = Arrays.asList( new String[]{ res.getName() } );

         for( Reservation resv : cal.getReservations() ){
            state.reservations.add( new ReservationBean( resv.getForUser(), resv.getFrom(), resv.getTo(), resourceAsList ) );
         }         
      }

      return state;

      //throw new UnsupportedOperationException("Not yet implemented");
   }




   /**
    * Load the state.
    */
   private void loadState() throws JawaBotIOException, UnknownResourceException {
      JaxbStatePersister sp = new JaxbStatePersister();
      log.info("Loading via: "+sp);
      StateBean state = sp.load();
      this.applyState( state );
   }


   /**
    * Save the state.
    * TODO: Make private again when stateChange is monitored in ResourceManager.
    */
   public void saveState() throws JawaBotIOException {
      JaxbStatePersister sp = new JaxbStatePersister();
      log.info("Saving via: "+sp);
      sp.save( this.extractState() );
   }


   /**
    *  Shutdown hook.
    */
   void waitForShutdown() {
      synchronized( this.shutdown ){
         try {
            this.shutdown.wait();
         } catch (InterruptedException ex) {
            log.warn("Interrupted.");
         }
      }
   }

   /**
    * This should let waiting threads (main?) continue and finish.
    */
   void notifyShutdown() {
      synchronized( this.shutdown ){
         log.warn("Shutting down.");
         this.shutdown.notifyAll();
      }
   }

   Object shutdown = new Object();




		
}// class


