package org.jboss.jawabot.plugin.irc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.jboss.weld.environment.se.jpa.EntitiesPackagesProvider;
import org.jboss.weld.environment.se.jpa.JpaTransactional;

/**
 *  Manages Channels.
 *  Also provides a list of packages which contain entities of this plugin.
 * 
 *  @author Ondrej Zizka
 */
@ApplicationScoped
public class ChannelManager implements EntitiesPackagesProvider {
   
   @Inject private EntityManager em;
   

   @JpaTransactional
   public synchronized boolean addChannel( Channel ch ) {
      em.persist(ch);
      return true;
   }

   @JpaTransactional
   public Channel byID( long id ){
      return em.find( Channel.class, id );
   }

   @JpaTransactional
   public Channel byName( String name ){
      try{
         return em.createQuery("SELECT ch FROM Channel ch WHERE ch.name = ?", Channel.class).setParameter(1, name).getSingleResult();
      }
      catch ( NoResultException ex ){
         return new Channel(name);
      }
   }

   @JpaTransactional
   public List<Channel> getChannelsRange_OrderByWhenDesc( int from, int offset ) {
      return em.createQuery("SELECT ch FROM Channel ch ORDER BY ch.name", Channel.class).setFirstResult(from).setMaxResults(offset).getResultList();
   }

   public List<Channel> getUsersInChannel(Channel channel) {
      return Arrays.asList( new Channel[]{ new Channel("#jbossqa"), new Channel("#seam"), new Channel("#richfaces") } );
   }


   
   
   @Override
   public Collection<String> getEntityPackages() {
      return Arrays.asList( new String[]{
         "org.jboss.jawabot.plugin.irc",
      });
   }

   @Override
   public Collection<Class> getEntityClasses() {
      return Collections.EMPTY_LIST;
   }

}// class
