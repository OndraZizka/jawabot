package org.jboss.jawabot.irc.plugin.logger;

import cz.dynawest.util.plugin.PluginEx;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.jboss.jawabot.irc.IIrcPluginHook;
import org.jboss.jawabot.irc.IrcPluginException;
import org.jboss.jawabot.irc.model.IrcMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Logger plugin - records the IRC messages to database.
 * 
 *  @author Ondrej Zizka
 */
public class LoggerIrcPluginHook implements IIrcPluginHook<Object> {
   private static final Logger log = LoggerFactory.getLogger( LoggerIrcPluginHook.class );

   @Inject EntityManager em;
   
   
   @Override
   public void onMessage(IrcMessage message) throws IrcPluginException {
      log.info(" IRC message: " + message);
      log.info(" em: " + em);
      em.persist( message );
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

