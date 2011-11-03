
package org.jboss.jawabot.irc;

import org.jboss.jawabot.IModuleHook;
import org.jboss.jawabot.config.JaxbConfigPersister;
import org.jboss.jawabot.config.beans.ConfigBean;

import org.apache.log4j.Logger;
import org.jboss.jawabot.JawaBot;
import org.jboss.jawabot.ex.JawaBotException;
import org.jboss.jawabot.ex.JawaBotIOException;
import org.jboss.jawabot.ex.UnknownResourceException;

/**
 *
 * @author ondra
 */
public class IrcModuleHook implements IModuleHook 
{
   private static final Logger log = Logger.getLogger(JaxbConfigPersister.class);
    
    
   private static JawaIrcBot bot;

   public static JawaIrcBot getBot() { return bot; }
    
   @Override
   public void initModule( JawaBot jawaBot, ConfigBean configBean ) throws JawaBotIOException, UnknownResourceException {
      bot = new JawaIrcBot( jawaBot );
      bot.applyConfig( configBean );
      bot.init();
   }

   @Override
   public void destroyModule() {
   }
    
   
   
   @Override
   public void applyConfig(ConfigBean configBean) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void mergeConfig(ConfigBean configBean) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void startModule() throws JawaBotException {
      bot.connectAndJoin();
   }

   @Override
   public void stopModule() {
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
