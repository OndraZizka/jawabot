package org.jboss.jawabot.irc.plugin.logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.jboss.jawabot.irc.IIrcPluginHook;
import org.jboss.jawabot.irc.IrcPluginException;
import org.jboss.jawabot.irc.model.IrcMessage;
import org.jboss.weld.environment.se.jpa.JpaTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Logger plugin - records the IRC messages to database.
 * 
 *  @author Ondrej Zizka
 */
public class LoggerIrcPluginHook implements IIrcPluginHook<Object> {
   private static final Logger log = LoggerFactory.getLogger( LoggerIrcPluginHook.class );

   @Inject EntityManager em; /// To be replaced with a service.
   
   @Inject JpaLoggerService loggerService;
   
   
   @Override
   @JpaTransactional
   public void onMessage(IrcMessage message) throws IrcPluginException {
      loggerService.storeMessage(message);
   }

   @Override
   public void initModule(Object initObject) throws IrcPluginException {
   }

   @Override
   public void startModule() throws IrcPluginException {
   }

   @Override
   public void stopModule() throws IrcPluginException {
   }

   @Override
   public void destroyModule() throws IrcPluginException {
   }
   

   
}// class

