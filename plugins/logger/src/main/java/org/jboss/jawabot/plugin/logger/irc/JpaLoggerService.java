package org.jboss.jawabot.plugin.logger.irc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.jboss.jawabot.irc.ent.IrcEvMessage;
import org.jboss.jawabot.irc.ent.IrcEvent;
import org.jboss.jawabot.plugin.logger.ent.ChannelSettings;
import org.jboss.weld.environment.se.jpa.IEntitiesPackagesProvider;
import org.jboss.weld.environment.se.jpa.JpaTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Takes care of storing the messages, searching, looking them up etc.
 * Also provides a list of packages which contain entities of this plugin.
 * 
 * @author Ondrej Zizka
 */
@ApplicationScoped // Must be here, otherwise http://seamframework.org/Community/CyclicInjectionUntilOutOfMemoryError
public class JpaLoggerService implements ILoggerService {
   private static final Logger log = LoggerFactory.getLogger( JpaLoggerService.class );
   
   @Inject EntityManager em;
   

   // Which channels are being logged.
   // Current behavior is to only log those explicitly enabled.
   private Map<String, Boolean> channelsEnabled = new HashMap();
   
   
   @PostConstruct
   @JpaTransactional
   private void initEnabledMap(){
      log.debug("Initializing channels enabled map...");
      List<ChannelSettings> chsList = em.createQuery("SELECT chs FROM ChannelSettings chs", ChannelSettings.class).getResultList();
      for (ChannelSettings chs : chsList) {
         this.channelsEnabled.put( chs.getChannel(), chs.isLoggingEnabled() );
      }
   }
   
   public boolean isLoggingEnabledForChannel( String ch ){
      return Boolean.TRUE == this.channelsEnabled.get( ch );
   }
   
   @JpaTransactional
   public void setLoggingEnabledForChannel( String ch, boolean enabled ){
      this.channelsEnabled.put( ch, enabled );
      try {
         ChannelSettings chs = em.createQuery("SELECT chs FROM ChannelSettings chs WHERE name = ?", ChannelSettings.class).setParameter(1, ch).getSingleResult();
         chs.setLoggingEnabled( enabled );
         em.merge( chs );
      }catch( NoResultException ex ){
         em.persist( new ChannelSettings( ch, enabled ) );
      }
   }
   
   
   
   @Override
   public void storeEvent(IrcEvent ev) {
      em.persist( ev );
   }

   @Override
   public void storeMessage( IrcEvMessage msg ){
      log.info(" IRC message: " + msg);
      log.info(" em: " + em);
      em.persist( msg );
      log.info(" Message saved: " + msg);
   }
   
   
   @Override
   public List<IrcEvMessage> getMessages( MessagesCriteria msgCriteria ){
      return em.createQuery("FROM IrcMessage m ORDER BY id DESC", IrcEvMessage.class).getResultList();
   }

}// class

