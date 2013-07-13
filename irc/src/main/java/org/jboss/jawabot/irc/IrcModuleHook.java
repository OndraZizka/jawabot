
package org.jboss.jawabot.irc;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.jboss.jawabot.IModuleHook;
import org.jboss.jawabot.config.beans.ConfigBean;

import org.jboss.jawabot.JawaBot;
import org.jboss.jawabot.ex.JawaBotException;
import org.jboss.jawabot.ex.JawaBotIOException;
import org.jboss.jawabot.ex.UnknownResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  IRC module for JawaBot. Handles IRC connection (PircBot) and manages the plugins (CDI).
 *
 * @author Ondrej Zizka
 */
public class IrcModuleHook implements IModuleHook<JawaBot, JawaBotException>
{
   private static final Logger log = LoggerFactory.getLogger(IrcModuleHook.class);
   
   
   
   private JawaIrcBot ircBot;
   public JawaIrcBot getBot() { return ircBot; }

   
   @Inject Instance<JawaIrcBot> jawaIrcBotInst;
   /*@Produces private JawaIrcBot createJawaIrcBot(){
      return new JawaIrcBot(null);
   }*/
   
   @Override
   public void initModule( JawaBot jawaBot, ConfigBean configBean ) throws JawaBotIOException, UnknownResourceException, JawaBotException {
      //this.bot = new JawaIrcBot( jawaBot );
      this.ircBot = jawaIrcBotInst.get();
      
      this.ircBot.setJawaBot( jawaBot );
      this.ircBot.applyConfig( configBean );
      this.ircBot.init();
   }
   
   @Override
   public void initModule(JawaBot jawaBot) throws JawaBotException {
      this.initModule( jawaBot, jawaBot.getConfig() );
   }


   @Override
   public void destroyModule() {
      this.ircBot.getConn().dispose();
   }
    
   
   
   @Override
   public void applyConfig(ConfigBean configBean) {
   }

   @Override
   public void mergeConfig(ConfigBean configBean) {
   }

   
   @Override
   public void startModule() throws JawaBotException {
      ircBot.connectAndJoin();
   }

   @Override
   public void stopModule() {
      ircBot.getConn().disconnect();
   }


   /**
    *  Adds an IRC appender to root logger.
    *
   private void resetIrcAppender() {
       final IrcAppender ircAppender = new IrcAppender( ircBot );
       ircAppender.setLayout( new PatternLayout("%d{HH:mm:ss.SSS} %-5p [%t] %c  %m%n") );
       org.apache.log4j.Logger.getRootLogger().addAppender( ircAppender);
   }/**/
    
}// class
