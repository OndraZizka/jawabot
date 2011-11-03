package org.jboss.jawabot.plugin.reserv.irc;

import java.util.logging.Level;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.jawabot.irc.IIrcPluginHook;
import org.jboss.jawabot.irc.IrcBotProxy;
import org.jboss.jawabot.irc.IrcPluginException;
import org.jboss.jawabot.irc.IrcPluginHookBase;
import org.jboss.jawabot.irc.IrcUtils;

import org.jboss.jawabot.ex.JawaBotIOException;
import org.jboss.jawabot.ex.JawaBotException;
import org.apache.commons.lang.StringUtils;
import org.jboss.jawabot.JawaBot;
import org.jboss.jawabot.config.beans.ConfigBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.jawabot.plugin.reserv.bus.ReservUtils;
import org.jboss.jawabot.MailData;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.MailUtils;
import org.jboss.jawabot.irc.CommandContext;
import org.jboss.jawabot.irc.CommandReply;
import org.jboss.jawabot.irc.CommandReplyMessage;
import org.jboss.jawabot.irc.ent.IrcEvMessage;
import org.jboss.jawabot.plugin.reserv.config.ReservPluginConfigBean;
import org.jboss.jawabot.plugin.reserv.bus.ReservService;
import org.jboss.jawabot.plugin.reserv.bus.ReservationWrap;
import org.jboss.jawabot.plugin.reserv.bus.Resource;
import org.jboss.jawabot.plugin.reserv.bus.ResourceManager.ReservationsBookingResult;


/**
 *  Automatically ops everyone in the channel.
 *  Also asks for operator if not having it.
 * 
 *  @author Ondrej Zizka
 */
@ApplicationScoped
public class ReservationIrcPluginHook extends IrcPluginHookBase implements IIrcPluginHook<Object> {
    private static final Logger log = LoggerFactory.getLogger( ReservationIrcPluginHook.class );

    private ReservPluginConfigBean config;
    private ConfigBean appConfig;

    
    @Inject ReservService reservService;
    
    @Inject ReservationCommandHandler commandHandler;
    
    @Inject MailUtils mailUtils;
        
        

    @Override
    public void initModule( Object initObject ) throws JawaBotException {
        if( ! ( initObject instanceof JawaBot ) )
            throw new JawaBotException("Expected JawaBot as initObject, got: " + initObject.getClass() );
        JawaBot jawaBot = (JawaBot) initObject;
        this.appConfig = jawaBot.getConfig();
        //this.config = this.loadConfig( appConfig.plugins.get("reserv") );
        this.config = this.loadConfig( "..." ); // TODO!
    }
    
    private ReservPluginConfigBean loadConfig( String path ){
        return new ReservPluginConfigBean(); // TODO!!
    }
    
   
    
   

    /**
     * Checks whether the msg starts with bot's nickname, and if so,
     * calls handleJawaBotCommand(), giving the channel name,
     * calling user's nick and the message.
     */
    @Override
    public void onMessage( IrcEvMessage msg, IrcBotProxy bot ) throws IrcPluginException
    {
        // Either process a command or dispatch the message to plugins.
        boolean wasCommand = false;
        
        String msgNorm = msg.getText().trim().toLowerCase();

        // Check for presence of bot nick prolog.
        int prologEnd = Math.max(
              0, // IrcUtils.getMsgStartAfterNick( msgNorm, USUAL_NICK ),
              IrcUtils.getMsgStartAfterNick( msgNorm, bot.getNick() ) );
        
        if( prologEnd != 0 ){

            // Get the rest of the message sans eventual starting colon.
            String command = msgNorm.substring( prologEnd );
            command = StringUtils.removeStart( command, ":").trim();
            command = StringUtils.removeStart( command, ",").trim();

            // and process the command.
            wasCommand = handleJawaBotCommand( msg.getChannel(), msg.getUser(), command, bot );
        }


    }// onMessage()




    @Override
    public void onPrivateMessage( IrcEvMessage message, IrcBotProxy bot ) throws IrcPluginException {
        
        
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
    private boolean handleJawaBotCommand( String fromChannel, String fromUser, final String commandOrig, IrcBotProxy bot ) {
        log.debug(String.format("%s %s %s", fromChannel, fromUser, commandOrig));

        //ReservationCommandHandler commandHandler = new ReservationCommandHandlerImpl( bot );

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


        // Take / Keep
        if( command.startsWith("take") || command.startsWith("keep") ) {
            wasValidCommand = true;

            String params = command.substring(4).trim();
            reply = commandHandler.onTake( ctx, params );

        }// take



        // Find
        else if ( command.startsWith("find") || command.startsWith("free") ) {
            wasValidCommand = true;

            String params = commandOrig.substring(4).trim();
            reply = commandHandler.onFind(ctx, params);

        }
        
        // Leave (resource)
        else if ( command.startsWith("leave") ) {
            cmd_leave:
            {
                wasValidCommand = true;

                String params = command.substring(5).trim();
                reply = commandHandler.onLeave(ctx, params);

            }// cmd_leave
        }
        
        // List
        else if (command.startsWith("list")) {
            wasValidCommand = true;

            String params = command.substring(4).trim();
            reply = commandHandler.onList(ctx, params);
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
                


        log.debug("Done processing command. Reply is: " + reply);

        

        // Temporarily in an if; after moving, throw if null.
        if( reply != null ) {
            // Temporary - copy the flag.
            stateChanged = reply.stateChanged;
            wasValidSyntax = !reply.reportInvalidSyntax;

            // Send IRC messages.

            // Default and debug channel.
            // TODO: Create ResourcePlugin ConfigBean.
            reply.additionalAnnounceChannels.add( this.getConfig().settings.announceDefaultChannel );
            reply.additionalAnnounceChannels.add( this.getAppConfig().settings.debugChannel );

            boolean noDangerOfReplyDubbing = ctx.isPrivate || !reply.additionalAnnounceChannels.contains( replyTo );

            // Send IRC messages.
            for( CommandReplyMessage msg : reply.ircMessages ) {
                if( msg.isAnnouncement ) {
                    for( String sendTo : reply.additionalAnnounceChannels ) {
                        bot.sendMessage( sendTo, msg.text );
                    }
                }
                // Prevent duplication: To send this reply, it must not go to the channel where it already was sent to.
                if( msg.isReply && (noDangerOfReplyDubbing || !msg.isAnnouncement) ) {
                    bot.sendMessage( replyTo, msg.text );
                }
            }

            // Send mail announcements.
            for( MailData mail : reply.mailAnnouncements ) {
                try {
                    trySendMail( mail, ctx.fromUserNorm, ctx.fromChannel );
                }
                catch( JawaBotException ex ) {
                    log.error(" Sending mail failed: " + ex, ex);
                    bot.sendDebugMessage(" Sending mail failed: " + ex );
                }
            }
        }


        // If the state changed, save.
        // TODO: Watch state changes in the reservationsManager.
        if( stateChanged ) {
            try {
                reservService.saveState();
                String msg = String.format( "State saved after %s on %s did command: %s", fromUser, fromChannel, commandOrig );
                log.info( msg );
                bot.sendDebugMessage( msg );
            }
            catch( JawaBotIOException ex ) {
                String msg = "Error saving state: " + ex.getMessage();
                log.error( msg, ex );
                bot.sendDebugMessage( msg );
            }
        }


        // Invalid command?
        if( !wasValidCommand ) {
            //sendMessage( replyTo, "Invalid command, see " + JawaBotApp.PROJECT_DOC_URL );
            // Nothing - plugins must checkt it too.
        }

        // Invalid syntax?
        if( !wasValidSyntax ) {
            bot.sendMessage( replyTo, "Invalid command syntax, see " + JawaBotApp.PROJECT_DOC_URL );
        }

        return wasValidCommand;

    }// handleJiraBotCommand()






   
   
    /**
     * Sends an announcement mail to a mailing list (currently jboss-qa-brno).
     * @deprecated in favor of #announceTakeOnMailingList( ReservationsBookingResult bookingResult, String customComment)
     */
    private void announceTakeOnMailingList(Resource resource, ReservationWrap reservation, String customComment) throws JawaBotException {
        ReservPluginConfigBean cnf = this.getConfig();
        String subject = ReservUtils.formatReservationInfoLine(resource.getName(), reservation);
        trySendMail( new MailData(subject, customComment), reservation.getForUser(), cnf.settings.announceDefaultChannel );
    }

    /**
     * Sends an announcement mail to a mailing list.
     * @deprecated  in favor of CommandHandlerImpl.createTakeAnnouncementMail() .
     */
    private void announceTakeOnMailingList( ReservationsBookingResult bookingResult, String customComment ) throws JawaBotException {
        ReservPluginConfigBean cnf = this.getConfig();
        String subject = ReservUtils.formatReservationInfoLine( bookingResult );

        // Message body.

        // Custom comment.
        StringBuilder sb = new StringBuilder();
        if( null != customComment ) {
            sb.append( customComment ).append( "\n" );
        }

        // List the reservations.
        if( bookingResult.resultingReservations.size() > 1 ) {
            for( ReservationWrap resvWrap : bookingResult.resultingReservations ) {
                sb.append( ReservUtils.formatReservationInfoLine( resvWrap.resourceName, resvWrap ) );
                sb.append( "\n" );
            }
        }

        String messageBody = sb.toString();

        trySendMail( new MailData(subject, messageBody), bookingResult.claimedResv.getForUser(), cnf.settings.announceDefaultChannel );
    }



    /**
     * Tries to send a mail; eventual failure is announced on the given channel.
     */
    private void trySendMail( MailData mail, String fromUser, String fallbackErrorMsgChannel ) throws JawaBotException {
        try {
            // Send the mail announcement.
            sendMail( fromUser, mail );
        }
        catch( JawaBotException ex ) {
            String excMessage = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            String reply = excMessage; //"Unable to send announcement email: "+excMessage;
            log.error( reply );
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

        String messageBody = (StringUtils.isBlank( mail.messageBody ) ? "" : mail.messageBody + "\n\n");
        messageBody += "Possible sender's e-mail: " + fromUser + "@redhat.com\nThis message was "
            + "generated by JawaBot " + JawaBotApp.VERSION + ".\n" + JawaBotApp.PROJECT_DOC_URL;
        mail.messageBody = messageBody;
        mail.fromName = fromUser + " via JawaBot";

        new MailUtils(null).sendMail( mail );

    }


    private ReservPluginConfigBean getConfig() {
        return this.config;
    }
    
    private ConfigBean getAppConfig() {
        return this.appConfig;
    }
    

   
   
}// class

