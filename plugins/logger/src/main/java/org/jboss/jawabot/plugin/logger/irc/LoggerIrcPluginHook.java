package org.jboss.jawabot.plugin.logger.irc;

import javax.inject.Inject;
import org.jboss.jawabot.irc.IIrcPluginHook;
import org.jboss.jawabot.irc.IrcBotProxy;
import org.jboss.jawabot.irc.IrcPluginException;
import org.jboss.jawabot.irc.IrcPluginHookBase;
import org.jboss.jawabot.irc.ent.IrcEvMessage;
import org.jboss.weld.environment.se.jpa.JpaTransactional;
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
      if( ! this.loggerService.isLoggingEnabledForChannel( msg.getChannel() ) )
         return;
      this.loggerService.storeMessage( msg );
   }

   @Override
   public void onPrivateMessage( IrcEvMessage message, IrcBotProxy bot ) throws IrcPluginException {
   }

   
}// class

