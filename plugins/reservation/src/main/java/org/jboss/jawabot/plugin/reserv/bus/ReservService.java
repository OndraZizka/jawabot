package org.jboss.jawabot.plugin.reserv.bus;

import org.jboss.jawabot.ex.UnknownResourceException;
import org.jboss.jawabot.ex.JawaBotIOException;
import org.jboss.jawabot.ex.JawaBotException;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.jboss.jawabot.FromJawaBot;
import org.jboss.jawabot.JawaBot;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.MailData;
import org.jboss.jawabot.config.JaxbGenericPersister;
import org.jboss.jawabot.config.beans.ConfigBean;
import org.jboss.jawabot.plugin.reserv.config.beans.ReservPluginConfigBean;
import org.jboss.jawabot.plugin.reserv.state.beans.StateBean;
import org.slf4j.Logger; import org.slf4j.LoggerFactory; 
import org.jboss.jawabot.plugin.reserv.bus.ResourceManager.ReservationsBookingResult;
import org.jboss.jawabot.plugin.reserv.state.JaxbStatePersister;
import org.jboss.jawabot.plugin.reserv.state.beans.ReservationBean;


/**
 *  
 *  @author Ondrej Zizka
 */
@ApplicationScoped
public class ReservService {
    private static final Logger log = LoggerFactory.getLogger( ReservService.class );

    private ReservPluginConfigBean config;

    //@Inject private JaxbReservPluginConfigPersister configPersister;
    //@Inject @FromJawaBot private ConfigBean jawabotConfig;
	@Inject private JawaBot jawaBot;


    // ResourceManager
    @Inject private ResourceManager resourceManager;
    //private ResourceManager resourceManager = new ResourceManager();
    //@Produces @FromJawaBot
    public ResourceManager getResourceManager() {      return resourceManager;   }
    public void setResourceManager(ResourceManager resourceManager) { this.resourceManager = resourceManager; }




    /** Returns a list of all reservation calendars. */
    Map<Resource, ReservationCalendar> getReservationCalendars(){
        return this.getResourceManager().getReservationCalendars();
    }
    void setReservationCalendars( Map<Resource, ReservationCalendar> cals ){
        this.getResourceManager().setReservationCalendars( cals );
    }

   
   
   
   
	/**
    * Init - loads the state (resources reservations) and stores the quit password.
    */
    @PostConstruct
    public synchronized void init() throws JawaBotIOException, UnknownResourceException {
        log.info("Initializing...");
        
        //String configPath = this.jawabotConfig.getPluginsMap().get("reservation");
				String configPath = this.jawaBot.getConfig().getPluginsMap().get("reservation");
        
        // Config
        JaxbGenericPersister<ReservPluginConfigBean> persister = 
            new JaxbGenericPersister( configPath, ReservPluginConfigBean.class );
        this.config = persister.load();
				this.applyConfig( this.config );

        //String statePath = "conf/JawaBotConfig-plugin-reservation"; // TODO: Take from config!!
        String statePath = this.config.settings.stateFilePath;
        StateBean state = new JaxbStatePersister().setFilePath(statePath).load();
        this.applyState( state );
    }
   
   

   
   
    /**
     * Sends an announcement mail to a mailing list (currently jboss-qa-brno).
     * @deprecated in favor of #announceTakeOnMailingList( ReservationsBookingResult bookingResult, String customComment)
     */
    private void announceTakeOnMailingList( Resource resource, ReservationWrap reservation, String customComment ) throws JawaBotException{
      ReservPluginConfigBean cnf = this.getConfig();
      String subject = ReservUtils.formatReservationInfoLine(resource.getName(), reservation);
      trySendMail( new MailData( subject, customComment ), reservation.getForUser(), cnf.settings.announceDefaultChannel);
    }



    /**
     * Sends an announcement mail to a mailing list.
     * @deprecated  in favor of CommandHandlerImpl.createTakeAnnouncementMail() .
     */
    private void announceTakeOnMailingList( ReservationsBookingResult bookingResult, String customComment ) throws JawaBotException
    {
        ReservPluginConfigBean cnf = this.getConfig();
        String subject = ReservUtils.formatReservationInfoLine( bookingResult );

        // Message body.

        // Custom comment.
        StringBuilder sb = new StringBuilder();
        if( null != customComment )
        sb.append(customComment).append("\n");

        // List the reservations.
        if( bookingResult.resultingReservations.size() > 1 ){
            for( ReservationWrap resvWrap : bookingResult.resultingReservations ){
                sb.append( ReservUtils.formatReservationInfoLine(resvWrap.resourceName, resvWrap) );
                sb.append("\n");
            }
        }

        String messageBody = sb.toString();

        trySendMail( new MailData( subject, messageBody ), bookingResult.claimedResv.getForUser(), cnf.settings.announceDefaultChannel );
    }

   
   


   /**
    * Tries to send a mail; eventual failure is announced on the given channel.
    */
   public void trySendMail( MailData mail, String fromUser, String fallbackErrorMsgChannel ) throws JawaBotException{
      try{
         // Send the mail announcement.
         sendMail( fromUser, mail );
      }
      catch( JawaBotException ex ){
         String excMessage = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
         String reply = excMessage; //"Unable to send announcement email: "+excMessage;
         log.error(reply);
         //bot.sendMessage( fallbackErrorMsgChannel, reply);
         throw new JawaBotException( reply );
      }
   }


   /**
    * Sends a mail announcement about user's action.
    */
   private void sendMail( String fromUser, MailData mail ) throws JawaBotException {

        ReservPluginConfigBean cnf = this.getConfig();

        log.debug( String.format( "Sending mail: host %s, to %s, from %s <%s>",
            cnf.settings.smtpHost,
            cnf.settings.announceEmailTo,
            fromUser,
            cnf.settings.announceEmailFrom ) );

        try {
            SimpleEmail email = new SimpleEmail();
            email.setHostName( cnf.settings.smtpHost );
            email.addTo( cnf.settings.announceEmailTo ); // TODO: Let it depend on the resource's group.
            email.setFrom( cnf.settings.announceEmailFrom, fromUser + " via JawaBot" );
            email.setSubject( mail.subject );
            String messageBody = (StringUtils.isBlank( mail.messageBody ) ? "" : mail.messageBody + "\n\n");
            email.setMsg( messageBody + "Possible sender's e-mail: " + fromUser + "@redhat.com\nThis message was "
                + "generated by JawaBot " + JawaBotApp.VERSION + ".\n" + JawaBotApp.PROJECT_DOC_URL );
            email.send();
        }
        catch( EmailException ex ) {
            throw new JawaBotException( "Can't mail to " + cnf.settings.announceEmailTo + ": " + ex.getMessage(), ex );
        }
        /*
        String messageBody = (StringUtils.isBlank( mail.messageBody ) ? "" : mail.messageBody + "\n\n");
        messageBody += "Possible sender's e-mail: " + fromUser + "@redhat.com\nThis message was "
            + "generated by JawaBot " + JawaBotApp.VERSION + ".\n" + JawaBotApp.PROJECT_DOC_URL;
        mail.messageBody = messageBody;
        mail.fromName = fromUser + " via JawaBot";

        new MailUtils( null ).sendMail( mail );
         */
        
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
   public void applyConfig( ReservPluginConfigBean config )
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
    *  TODO:  Make private again;  Refactor ReservationCommandHandlerImpl to only call this service's methods.
    */
    public ReservPluginConfigBean getConfig() {
        return this.config;
    }
    
    
}// class

