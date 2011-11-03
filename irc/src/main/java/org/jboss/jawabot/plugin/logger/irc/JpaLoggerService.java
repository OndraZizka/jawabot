package org.jboss.jawabot.plugin.logger.irc;

import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.jboss.jawabot.irc.model.IrcMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Takes care of storing the messages, searching, looking them up etc.
 * 
 * @author Ondrej Zizka
 */
public class JpaLoggerService implements ILoggerService {
   private static final Logger log = LoggerFactory.getLogger( JpaLoggerService.class );
   
   @Inject EntityManager em;
   
   @Override
   public void storeMessage( IrcMessage msg ){
      log.info(" IRC message: " + msg);
      log.info(" em: " + em);
      em.persist( msg );
      log.info(" Message saved: " + msg);
   }
   
   
   @Override
   public List<IrcMessage> getMessages( MessagesCriteria msgCriteria ){
      return em.createQuery("FROM IrcMessage m ORDER BY id DESC", IrcMessage.class).getResultList();
   }
   
}// class

