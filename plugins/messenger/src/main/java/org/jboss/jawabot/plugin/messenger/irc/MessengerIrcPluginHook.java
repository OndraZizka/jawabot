package org.jboss.jawabot.plugin.messenger.irc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import javax.inject.Inject;
import org.jboss.jawabot.irc.IIrcPluginHook;
import org.jboss.jawabot.irc.IrcBotProxy;
import org.jboss.jawabot.irc.IrcPluginException;
import org.jboss.jawabot.irc.IrcPluginHookBase;
import org.jboss.jawabot.irc.IrcUtils;
import org.jboss.jawabot.irc.ent.IrcEvMessage;
import org.jibble.pircbot.User;
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
		
		// Here we store messages before intent to deliver is confirmed.
		Map<String, LeftMessage> leftMessagesCache = new HashMap();
		
		
		private static final int MIN_USER_COUNT_TO_SCAN_CHANNEL = 10;
    

    // TODO: Prevent multiple scanning over this.
    Set<String> channelsBeingScanned = new ConcurrentSkipListSet();
    
    
    
    // IRC stuff.


    @Override
    public void onMessage( IrcEvMessage msg, IrcBotProxy bot ) throws IrcPluginException {
				// Answer for our question, see the else branch.
				if( msg.getText().startsWith("yes") ){
						LeftMessage post = this.leftMessagesCache.get( msg.getUser() );
						if( post == null )
								bot.sendReplyTo(msg, "Huh? Sorry I forgot what I asked.");
						else
								this.messengerService.leaveMessage( post );
								bot.sendReplyTo(msg, "Ok, stored.");
				}
				// Check whether the message is for some user.
				// If so, check if he's in this channel.
				// TODO: If not, check if he's in some channel where we joined. Perhaps in cooperation with WhereIs plugin.
				// If not, ask if we should deliver. The answer will be handled above.
				else{
						List<String> recipients = IrcUtils.whoIsThisMsgFor( msg.getText() );
						if( recipients.isEmpty() )
								return;

						if( bot.isUserInChannel( msg.getChannel(), recipients.get(0), true ) )
								return;
						
						bot.sendMessage( msg.getChannel(), msg.getUser(), recipients+" is not here. Do you want to leave him a message? (reply \"yes\")");
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
						bot.sendMessage(msg.getUser(), msg.getChannel(), "Correct format: send <nick>: <your message>");
						return;
				}
				
				msg.setRecipient( recp.get(0) );
				this.messengerService.leaveMessage(msg);
				bot.sendMessage(msg.getUser(), msg.getChannel(), "Message stored.");
    }
    
    
   
    /**
		 *  On join, check that user's messages, and eventually delivers them.
		 *  Private messages are sent privately, msgs from channel publicly, on the channel where user joined.
		 */
    @Override
    public void onJoin( String channel, String nick, IrcBotProxy bot  ) {
				this.notifyMessagesForNick( channel, nick, bot  );
		}
				
		
		private void notifyMessagesForNick( String channel, String nick, IrcBotProxy bot  ) {
				nick = IrcUtils.normalizeUserNick( nick );
				
				List<LeftMessage> msgs = this.messengerService.getMessagesForUser( nick, true );
				for( LeftMessage msg : msgs ) {
						if( null != msg.getChannel() ){
								String notice = msg.getUser() + " sent you "+msg.getWhen()+" in "+msg.getChannel()+": " + msg.getText();
								bot.sendMessage( nick, channel, notice);
						}
						else {
								String notice = msg.getUser() + " sent you "+msg.getWhen()+" privately: " + msg.getText();
								bot.sendMessage( nick, null, notice );
						}
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
