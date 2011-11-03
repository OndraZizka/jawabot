
package org.jboss.jawabot.plugin.reserv.irc;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.slf4j.Logger; import org.slf4j.LoggerFactory; 
import org.apache.commons.lang.StringUtils;
import org.jboss.jawabot.plugin.reserv.bus.ReservUtils;
import org.jboss.jawabot.MailData;
import org.jboss.jawabot.ex.JawaBotException;
import org.jboss.jawabot.irc.CommandContext;
import org.jboss.jawabot.irc.CommandReply;
import org.jboss.jawabot.plugin.reserv.bus.ReservService;
import org.jboss.jawabot.plugin.reserv.bus.Reservation;
import org.jboss.jawabot.plugin.reserv.bus.ReservationWrap;
import org.jboss.jawabot.plugin.reserv.bus.Resource;
import org.jboss.jawabot.plugin.reserv.bus.ResourceManager;
import org.jboss.jawabot.plugin.reserv.bus.ResourceManager.ReservationsBookingResult;

/**
 *  Handles received text commands, using CommandContext as input 
 *  and CommandReply as output.
 *
 *  TODO: This is de-facto a command parser + a service, we should split it.
 *     1) Rename onXXX() to parseXXX() 
 *     2) Split this class to CommandParser and ReservationService.
 *
 *
 * @author Ondrej Zizka
 */
public class ReservationCommandHandlerImpl implements ReservationCommandHandler
{
   private static final Logger log = LoggerFactory.getLogger( ReservationCommandHandlerImpl.class );

   
   @Inject ResourceManager resourceManager;
   @Inject ReservService   reservService;
   
   

   /**
    *  Take command - user asks for resource(s) for certain period.
    *  E.g.
    *        take jawa11,jawa12 +3  # Testing something.
    */
   @Override
   public CommandReply onTake( CommandContext ctx, String params ) {
      
      CommandReply reply = new CommandReply();

      //ResourceManager resourceManager = this.getJawaBot().getResourceManager();

      do {

         if( "".equals( params ) ){
            reply.addReply("Take what? Try 'list' for a list of resources.");
            return reply;
         }

         // TODO: Regex parsing?
         /*
         final String tName = "[a-z]\\p{Alnum}+";
         final String tNames = tName+"(,"+tName+")*";
         final String tDate = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
         final String tDaySpec = "("+tDate+")|\\+[0-9]+|today|mon|tue|wed|thu|fri|sat|sun";
         final String regex = "take( +"+tNames+")( +"+tDaySpec+")( +"+tDaySpec+")?#(.*)";
         */

         // Split the command and the custom reservation message.
         String customComment = StringUtils.substringAfter( params, "#").trim();
         if( "".equals( customComment ) )
            customComment = null;
         params = StringUtils.substringBefore( params, "#" ).trim();

         log.debug("Command: "+params+"  Comment: "+customComment);



         String parts[] = params.split(" ");
         String resNames = parts[0];

         Reservation datesHolder;
         try {
            params = params.substring( resNames.length() );
            datesHolder = Reservation.parseDates(params);
         }catch( ParseException ex ){
            reply.addReply( ex.getMessage() );
            break;
         }
         Date fromDate = datesHolder.getFrom();
         Date toDate = datesHolder.getTo();


         // Make a reservation.
         try {

            //ReservationWrap resultingReservation = resourceManager.bookResources(resName, fromUserNorm, fromDate, toDate);
            ReservationsBookingResult bookingResult = resourceManager.bookResources(resNames, ctx.fromUserNorm, fromDate, toDate);

            // Collision with another reservation.
            if( bookingResult.type == ReservationWrap.Type.COLLISION ){
               for( ReservationWrap resvWrap : bookingResult.resultingReservations ){
                  String twoDatesStr = ReservUtils.formatTwoDatesString( resvWrap.getFrom(), resvWrap.getTo(), false);
                  // Reservation collision!  jawa18 is reserved for ozizka from 2009-11-09 to 2009-11-15.
                  String msg = String.format("Reservation collision!  %s is reserved for %s %s.",
                     resvWrap.getResourceName(),
                     resvWrap.getForUser(),
                     twoDatesStr );
                  reply.addReply( msg );
               }
               break; // Konecna.
            }


            // No collision or merged with older, we're free to go.
            {
               reply.stateChanged = true;

               // Send the IRC reply.

               String twoDatesStr = ReservUtils.formatTwoDatesString(fromDate, toDate, false);

               // E.g.: jawa18 was succesfully kept for ozizka from 2009-11-27 to 2009-12-02.
               final String RESERVATION_OK_FORMAT = "%s was succesfully %s for %s %s.";
               // TODO: Care about this in the CommandReply receiver ( == CommandHandler caller).
               //String announceTo = StringUtils.defaultString(ctx.fromChannel, this.getConfig().settings.announceDefaultChannel);
               for( ReservationWrap resvWrap : bookingResult.resultingReservations ){
                  String msg = String.format( RESERVATION_OK_FORMAT,
                                 resvWrap.getResourceName(),
                                 resvWrap.type == ReservationWrap.Type.MERGED ? "kept" : "reserved",
                                 ctx.fromUserNorm,
                                 twoDatesStr
                              );
                  reply.addBoth( msg );
               }


               // Send the mail announcement, take care of errors.
               //announceTakeOnMailingList( resourceManager.getResource(resNames), bookingResult, customComment );
               //announceTakeOnMailingList( bookingResult, customComment );
               MailData mail = createTakeAnnouncementMail( bookingResult, customComment );
               reply.mailAnnouncements.add( mail );
               reply.mailAnnounceRecipients.add( this.reservService.getConfig().settings.announceEmailTo ); // Currently only default.
               
               // TODO: Add additional e-mails and IRC channels to send to, depending on resource group etc.
               //reply.additionalAnnounceChannels.add( ... );
               //reply.mailAnnounceRecipients.add( ... );

            }
            
            reply.wasSuccessful = true;
         }
         catch( JawaBotException ex ) {
            reply.addReply( ex.getMessage() );
            log.error( "Exception during 'take': "+ex, ex );
            break;
         }
         
      } while( false );

      return reply;

   }// onTake()








   /**
    * Parboiled mail - created from given booking result and comment.
    * TBD: Make private when not used from JawaBot (after moving is done).
    */
   /*private*/ static MailData createTakeAnnouncementMail( ReservationsBookingResult bookingResult, String customComment ) {
      
      String subject = ReservUtils.formatReservationInfoLine( bookingResult );

      // Message body.

      // Custom comment.
      StringBuilder mailBodySB = new StringBuilder();
      if( null != customComment )
         mailBodySB.append(customComment).append("\n");

      // List the reservations.
      if( bookingResult.resultingReservations.size() > 1 ){
         for( ReservationWrap resvWrap : bookingResult.resultingReservations ){
            mailBodySB.append( ReservUtils.formatReservationInfoLine(resvWrap.resourceName, resvWrap) );
            mailBodySB.append("\n");
         }
      }

      String messageBody = mailBodySB.toString();

      return new MailData( subject, messageBody );

   }







   /**
    *  Find
    */
   @Override
   public CommandReply onFind( CommandContext ctx, String params ) {
      CommandReply reply = new CommandReply();
      do{
         Reservation datesHolder;
         try{
            datesHolder = Reservation.parseDates(params);
         }catch(ParseException ex){
            reply.addReply( ex.getMessage() );
            break;
         }

         Date fromDate = datesHolder.getFrom();
         Date toDate = datesHolder.getTo();
         String twoDatesStr = ReservUtils.formatTwoDatesString(fromDate, toDate, false);
         
         List<Resource> freeResources = this.resourceManager.findFreeResources( fromDate, toDate );
         Collections.sort( freeResources );
         
         String message = String.format("Free "+twoDatesStr+": "+ StringUtils.join(freeResources, ", ") );
         reply.addReply( message );
      }
      while( false );

      reply.wasSuccessful = true;
      return reply;
   }


   /**
    *   Leave resource (cancel reservation).
    */
   @Override
   public CommandReply onLeave( CommandContext ctx, String params ) {

      CommandReply reply = new CommandReply();

      if( "".equals( params )){
         reply.addReply("Leave what? Try 'list', 'list <resource>' or 'help'. (and 'list <user>' in the future).");
         return reply;
      }

      String parts[] = params.split(" ");
      String resName = parts[0];

      String leftResourcesStr = null;

      // Try to leave, or
      try {
         if( "all".equals(resName) ){
            log.debug("Leaving ALL resources.");
            List<Resource> leftResources = this.resourceManager.leaveAll( ctx.fromUserNorm );
            leftResourcesStr = StringUtils.join( leftResources, ", " );
         }
         else {
            log.debug("Leaving one resource: "+resName);
            this.resourceManager.leave( resName, ctx.fromUserNorm );
            leftResourcesStr = resName;
         }
      } catch (JawaBotException ex) {
         log.debug("Can't leave.", ex);
         reply.addReply( "You can't leave "+resName+": "+ex.getMessage() );
         return reply;
      }

      // State changed.
      reply.stateChanged = true;

      // Announce on IRC.
      //String announceToChannel = StringUtils.defaultString( fromChannel, this.getConfig().settings.announceDefaultChannel ); TBD: Move up.
      String msg = String.format("%s has left %s.",  ctx.fromUserNorm, leftResourcesStr );
      reply.addBoth( msg ); // Or "You left...".

      // Announce on mailing list.
      reply.mailAnnouncements.add( new MailData("Leaving " + leftResourcesStr, "") );
      reply.mailAnnounceRecipients.add( this.reservService.getConfig().settings.announceEmailTo ); // Currently only default.

      reply.wasSuccessful = true;
      return reply;
      
   }// onLeave()


   

   /**
    *  List all resources.
    */
   @Override
   public CommandReply onList( CommandContext ctx, String params ) {
      CommandReply reply = new CommandReply();
      reply.wasSuccessful = true; // What is this good for, anyway. TODO: Remove?

      do{
         // List resources.
         if( "".equals( params ) ){
            reply.addReply( this.resourceManager.listResourcesAsString() );
            break;
         }

         // List the given resource.
         String[] parts = params.split(" ");
         String resName = parts[1];
         try {
            String list = this.resourceManager.printResourceReservations( resName );
            for( String line : list.split("\n") ){
               reply.addReply( line );
            }
         }
         catch( JawaBotException ex ){
            reply.addReply( ex.getMessage() );
         }
      }while( false );
      
      return reply;
   }


}// class
