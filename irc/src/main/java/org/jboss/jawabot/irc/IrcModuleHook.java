
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
public class IrcModuleHook implements IModuleHook 
{
   private static final Logger log = LoggerFactory.getLogger(IrcModuleHook.class);
    
   
   
   private static JawaIrcBot bot;

   public static JawaIrcBot getBot() { return bot; }
    
   @Override
   public void initModule( JawaBot jawaBot, ConfigBean configBean ) throws JawaBotIOException, UnknownResourceException, JawaBotException {
      bot = new JawaIrcBot( jawaBot );
      bot.applyConfig( configBean );
      bot.init();
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

   
   
   /**
    * Creates the bot according to the given config file, initializes it, runs it,
    * and waits until the bot thread shuts down.
    *
    * @param configFilePath    Path to the configuration file.
    * @throws JawaBotException
    * @deprecated  Split into init() and start().
    */
   /*private static void createAndRunTheBot() throws JawaBotException {

      String configFilePath = System.getProperty("config", "JawaBotConfig.xml");
      ConfigBean configBean = new JaxbConfigPersister(configFilePath).load();
      
      bot = new JawaIrcBot(  );
      bot.applyConfig( configBean );
      bot.init();
      bot.connectAndJoin();

      // Wait for the end.
      try {
         synchronized(bot){
            bot.wait();
         }
      } catch (InterruptedException ex) {
         log.info("Interrupted.");
      }

   }*/

   
   
    
}// class
