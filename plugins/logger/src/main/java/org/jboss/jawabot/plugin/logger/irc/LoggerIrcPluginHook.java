package org.jboss.jawabot.plugin.logger.irc;

import javax.inject.Inject;
import org.jboss.jawabot.irc.IIrcPluginHook;
import org.jboss.jawabot.irc.IrcBotProxy;
import org.jboss.jawabot.irc.IrcPluginException;
import org.jboss.jawabot.irc.IrcPluginHookBase;
import org.jboss.jawabot.irc.ent.IrcEvAction;
import org.jboss.jawabot.irc.ent.IrcEvJoin;
import org.jboss.jawabot.irc.ent.IrcEvMessage;
import org.jboss.jawabot.irc.ent.IrcEvNickChange;
import org.jboss.jawabot.irc.ent.IrcEvPart;
import org.jboss.weld.environment.se.jpa.JpaTransactional;
import org.jibble.pircbot.beans.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Logger plugin - records the IRC messages to database.
 * 
 *  @author Ondrej Zizka
 */
public class LoggerIrcPluginHook extends IrcPluginHookBase implements IIrcPluginHook<Object> {
   private static final Logger log = LoggerFactory.getLogger( LoggerIrcPluginHook.class );

   @Inject JpaLoggerService loggerService;
   
   
   // IRC stuff.
   
   @Override
   @JpaTransactional
   public void onMessage( IrcEvMessage msg, IrcBotProxy bot ) throws IrcPluginException {
      
      String txt = msg.getText();
      
      // Commands
      if( txt.startsWith("log ") ){
         if( txt.startsWith("log on") ){
            this.loggerService.setLoggingEnabledForChannel(msg.getChannel(), true);
            bot.sendReplyTo(msg, "Logging for this channel enabled.");
            return;
         }
         if( txt.startsWith("log off") ){
            this.loggerService.setLoggingEnabledForChannel(msg.getChannel(), false);
            bot.sendReplyTo(msg, "Logging for this channel disabled.");
            return;
         }
      }
      
      // Was not a command, log the message.
      if( ! this.loggerService.isLoggingEnabledForChannel( msg.getChannel() ) ){
         log.debug("Logging not enabled for this channel: " + msg.getChannel() );
         return;
      }
      this.loggerService.storeEvent( msg );
   }

   
    @Override
    @JpaTransactional
    public void onBotJoinChannel(String channel, IrcBotProxy bot) {
        boolean en = this.loggerService.isLoggingEnabledForChannel(channel);
        if( en ){
            bot.sendMessage(channel, "Logging for this channel " +(en ? "en" : "dis")+ "abled. (Change with \"log on\"/\"log off\".)");
        }
    }

    @Override
    @JpaTransactional
    public void onJoin(IrcEvJoin event, IrcBotProxy bot) {
        if( this.loggerService.isLoggingEnabledForChannel( event.getChannel() ) )
            this.loggerService.storeEvent( event );
    }

    @Override
    @JpaTransactional
    public void onPart(IrcEvPart event, IrcBotProxy bot) {
        if( this.loggerService.isLoggingEnabledForChannel( event.getChannel() ) )
            this.loggerService.storeEvent( event );
    }

    @Override
    @JpaTransactional
    public void onAction(IrcEvAction event, IrcBotProxy bot) {
        if( this.loggerService.isLoggingEnabledForChannel( event.getChannel() ) )
            this.loggerService.storeEvent( event );
    }

    /**
     *   Nick change event does not carry channel info.
     *   This method scans channels we're in for given nick.
     *   If found, logs the event in that channel(s).
     * 
     *   TBC: Is the new nick is already reflected?
     */
    @Override
    //@JpaTransactional
    public void onNickChange( IrcEvNickChange ev, IrcBotProxy bot ) {
        
        // For all channels we are in...
        for( String ch : bot.getChannels() ){
            
            Boolean in1 = bot.isUserInChannel( ch, ev.getNewNick() );
            Boolean in2 = bot.isUserInChannel( ch, ev.getUser() );
            // null -> We are not in the channel so we don't know. Shouldn't happen, but to be safe.
            if( in1 == null || in2 == null ) continue;
            
            // The user changing nick is not in the channel.
            if( !( in1 || in2 ))  continue;
            
            if( ! this.loggerService.isLoggingEnabledForChannel( ch ) ) continue;
            ev.setChannel(ch);
            //log.debug("Saving: " + ev);
            this.loggerService.storeEvent( ev );
        }
        ev.setChannel(null);
    }

   
}// class

