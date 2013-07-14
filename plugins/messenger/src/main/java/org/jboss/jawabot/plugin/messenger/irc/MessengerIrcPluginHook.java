package org.jboss.jawabot.plugin.messenger.irc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jboss.jawabot.irc.IIrcPluginHook;
import org.jboss.jawabot.irc.IrcBotProxy;
import org.jboss.jawabot.irc.IrcPluginException;
import org.jboss.jawabot.irc.IrcPluginHookBase;
import org.jboss.jawabot.irc.IrcUtils;
import org.jboss.jawabot.irc.ent.IrcEvJoin;
import org.jboss.jawabot.irc.ent.IrcEvMessage;
import org.jibble.pircbot.beans.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  Scans all channels for users, and on request, tells where given user is or was.
 * 
 *  Every X minutes (configurable interval), visits all channels and updates the info.
 * 
 *  @author Ondrej Zizka
 */
public class MessengerIrcPluginHook extends IrcPluginHookBase implements IIrcPluginHook<Object> {
    private static final Logger log = LoggerFactory.getLogger( MessengerIrcPluginHook.class );
    private static final Logger logScan = LoggerFactory.getLogger( MessengerIrcPluginHook.class.getName()+".channelScanQueueProcessor" );
    
    @Inject protected MemoryMessengerService messengerService;
        
    // Here we store messages before intent to store for later delivery is confirmed.
    Map<String, LeftMessage> leftMessagesCache = new HashMap();

    

    
    // IRC stuff.


    @Override
    public void onMessage( IrcEvMessage msg, IrcBotProxy bot ) throws IrcPluginException {
        // Answer for our question, see the else branch.
        if( msg.getText().startsWith("yes") ){
            LeftMessage post = this.leftMessagesCache.get( msg.getUser() );
            if( post == null ){
                if(  msg.getChannel() == null )
                        bot.sendReplyTo(msg, "Huh? Sorry I forgot what I asked.");
            }
            else {
                this.messengerService.leaveMessage( post );
                bot.sendReplyTo(msg, "Ok, stored.");
            }
        }
        // Check whether the message is for some user.
        // If so, check if he's in this channel.
        // TODO: If not, check if he's in some channel where we joined. Perhaps in cooperation with WhereIs plugin.
        // If not, ask if we should deliver. The answer will be handled above.
        else{
            List<String> recipients = IrcUtils.whoIsThisMsgFor( msg.getText(), true );
            
            this.messengerService.removeBlockedRecipients(recipients);
            
            if( recipients.isEmpty() )
                return;

            if( bot.isUserInChannel( msg.getChannel(), recipients.get(0), true ) )
                return;

            bot.sendReplyTo( msg,  recipients.get(0)+" is not here. Do you want to leave a message? (reply \"yes\")");
            this.leftMessagesCache.put( msg.getUser(), new LeftMessage(msg) );
        }
    }


    /**
     *  TODO: 
     *  If a message starts with "post" or "send", stores a message for other user.
     */
    @Override
    public void onPrivateMessage( IrcEvMessage msg, IrcBotProxy bot ) throws IrcPluginException {
        if( ! msg.getText().startsWith("post ") && ! msg.getText().startsWith("send ") )
            return;

        String command = msg.getText().substring(5);
        List<String> recp = IrcUtils.whoIsThisMsgFor(command);
        if( recp.size() != 1 ){
            bot.sendMessage( msg.getUser(), msg.getChannel(), "Correct format: send <nick>: <your message>");
            return;
        }

        msg.setRecipient( recp.get(0) );
        this.messengerService.leaveMessage(msg);
        bot.sendMessage( msg.getUser(), msg.getChannel(), "Message stored.");
    }
    
    
   
    /**
         *  On join, check that user's messages, and eventually delivers them.
         *  Private messages are sent privately, msgs from channel publicly, on the channel where user joined.
         */
    @Override
    public void onJoin( IrcEvJoin event, IrcBotProxy bot  ) {
        this.notifyMessagesForNick( event.getChannel(), event.getUser(), bot );
    }

    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-DD H:m:s");

    private void notifyMessagesForNick( String channel, String nick, IrcBotProxy bot  ) {
        nick = IrcUtils.normalizeUserNick( nick );

        List<LeftMessage> msgs = this.messengerService.getMessagesForUser( nick, true );
        for( LeftMessage msg : msgs ) {
            String when = DF.format( msg.getWhen() );
            // ozizka: rhusar sent you 20
            String notice = msg.getUser() + " sent you " + when +
                    ( msg.getChannel() == null ? (" in "+msg.getChannel()+": ")  :  " privately: " )
                    + msg.getText();
            bot.sendMessage( nick, ( msg.getChannel() == null ? null : channel ), notice);
        }
    }


    /**
     *  When the bot joins a channel, it scans nicks and check their messages.
     *  Sends them if 
     * @param channel
     * @param bot 
     */
    @Override
    public void onBotJoinChannel( String channel, IrcBotProxy bot ) {
        log.debug("  onBotJoinChannel(): " + channel);
        for( User user : bot.getUsers( channel ) ){
            this.notifyMessagesForNick( channel, user.getNick(), bot  );
        }
    }

    
   
}// class
