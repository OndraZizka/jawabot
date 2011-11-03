
package org.jboss.jawabot.plugin.pastebin;

import org.jboss.jawabot.plugin.pastebin.ent.PasteBinEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.jboss.weld.environment.se.jpa.IEntitiesPackagesProvider;
import org.jboss.weld.environment.se.jpa.JpaTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Manages PasteBin entries.
 *  Also provides a list of packages which contain entities of this plugin.
 * 
 *  @author Ondrej Zizka
 */
@ApplicationScoped
public class JpaPasteBinManager implements IPasteBinManager
{
   private static final Logger log = LoggerFactory.getLogger(JpaPasteBinManager.class);
   
   
   @Inject private EntityManager em;
   

   @JpaTransactional
   public synchronized boolean addEntry( PasteBinEntry e ) {
      e.setWhen( new Date() );
      em.persist(e);
      return true;
   }

   @JpaTransactional
   public PasteBinEntry getById( long id ){
      return em.find( PasteBinEntry.class, id );
   }

   @JpaTransactional
   public PasteBinEntry getPaste( long id ) {
      return this.getById(id);
   }

   @JpaTransactional
   @Override
   public List<PasteBinEntry> getLastPastes_OrderByWhenDesc( int i ) {
      //em.getCriteriaBuilder().createQuery( PasteBinEntry.class );
      return em.createQuery("SELECT pbe FROM PasteBinEntry pbe ORDER BY pbe.when DESC", PasteBinEntry.class).setMaxResults(i).getResultList();
   }

   
}// class
