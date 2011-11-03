
package org.jboss.jawabot.irc;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
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
    
   
   
   private JawaIrcBot bot;
   public JawaIrcBot getBot() { return bot; }

   
   @Inject Instance<JawaIrcBot> jawaIrcBotInst;
   /*@Produces private JawaIrcBot createJawaIrcBot(){
      return new JawaIrcBot(null);
   }*/
   
   @Override
   public void initModule( JawaBot jawaBot, ConfigBean configBean ) throws JawaBotIOException, UnknownResourceException, JawaBotException {
      //this.bot = new JawaIrcBot( jawaBot );
      this.bot = jawaIrcBotInst.get();
      this.bot.setJawaBot( jawaBot );
      this.bot.applyConfig( configBean );
      this.bot.init();
   }
   
   @Override
   public void initModule(JawaBot jawaBot) throws JawaBotException {
      this.initModule( jawaBot, jawaBot.getConfig() );
   }


   @Override
   public void destroyModule() {
      bot.dispose();
   }
    
   
   
   @Override
   public void applyConfig(ConfigBean configBean) {
   }

   @Override
   public void mergeConfig(ConfigBean configBean) {
   }

   
   @Override
   public void startModule() throws JawaBotException {
      bot.connectAndJoin();
   }

   @Override
   public void stopModule() {
      bot.disconnect();
   }

   
    
}// class
