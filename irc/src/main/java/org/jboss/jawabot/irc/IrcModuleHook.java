
package org.jboss.jawabot.irc;

import org.jboss.jawabot.IModuleHook;
import org.jboss.jawabot.config.beans.ConfigBean;

import org.jboss.jawabot.JawaBot;
import org.jboss.jawabot.ex.JawaBotException;
import org.jboss.jawabot.ex.JawaBotIOException;
import org.jboss.jawabot.ex.UnknownResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Ondrej Zizka
 */
public class IrcModuleHook implements IModuleHook<JawaBot, JawaBotException>
{
   private static final Logger log = LoggerFactory.getLogger(IrcModuleHook.class);
    
   
   
   private JawaIrcBot bot;
   public JawaIrcBot getBot() { return bot; }

   
   
   @Override
   public void initModule( JawaBot jawaBot, ConfigBean configBean ) throws JawaBotIOException, UnknownResourceException, JawaBotException {
      this.bot = new JawaIrcBot( jawaBot );
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
