
package org.jboss.jawabot.usermgr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.apache.commons.lang.StringUtils;
import org.jboss.jawabot.state.ent.User;
import org.jboss.weld.environment.se.jpa.JpaTransactional;

/**
 *  
 *  @author Ondrej Zizka
 */
public class UserManager {

   private final List<User> users = new ArrayList();
   
   @Inject EntityManager em;

   public UserManager() {
      String[] names = StringUtils.split("ozizka plavice rhusar istudens rsvoboda lpetrovi");
      Arrays.sort(names);
      for( String name : names ) {
         this.users.add( new User( name ) );
      }
   }

   @JpaTransactional
   public List<User> getUsersRange_OrderByName( int from, int offset ){
      return em.createQuery("SELECT u FROM User u ORDER BY u.name", User.class).setFirstResult(from).setMaxResults(offset).getResultList();
   }

   @JpaTransactional
   public User byID( long userId ) {
      return em.find( User.class, userId );
   }

   @JpaTransactional
   public User byName( String userName ) {
      return em.createQuery("SELECT u FROM User u WHERE u.name = ?", User.class).setParameter( 1, userName ).getSingleResult();
   }

   @JpaTransactional
   public List<User> getUsersNameStartsWith( String start, int from, int offset ) {
      return em.createQuery("SELECT u FROM User u WHERE name LIKE '?%' ORDER BY u.name", User.class)
              .setParameter( 1, start )
              .setFirstResult(from).setMaxResults(offset).getResultList();
   }


}// class
