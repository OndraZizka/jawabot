package org.jboss.jawabot.irc.plugin.logger;

import cz.dynawest.util.plugin.PluginEx;
import org.jboss.jawabot.irc.IIrcPluginHook;
import org.jboss.jawabot.irc.IrcPluginException;
import org.jboss.jawabot.irc.model.IrcMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  
 *  @author Ondrej Zizka
 */
public class LoggerIrcPluginHook implements IIrcPluginHook<Object> {
   private static final Logger log = LoggerFactory.getLogger( LoggerIrcPluginHook.class );

   
   @Override
   public void onMessage(IrcMessage message) throws IrcPluginException {
      log.info(" IRC message: " + message);
       
   }

   @Override
   public void initModule(Object initObject) throws PluginEx {
   }

   @Override
   public void startModule() throws PluginEx {
   }

   @Override
   public void stopModule() throws PluginEx {
   }

   @Override
   public void destroyModule() throws PluginEx {
   }
   

   
}// class

