
package org.jboss.jawabot.usermgr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.apache.commons.lang.StringUtils;
import org.jboss.jawabot.state.ent.User;

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


   public List<User> getUsersRange_OrderByName(int from, int to){
      // TODO: Impl
      return this.users;
   }

   public User byID( long userId ) {
      // TODO: Impl
      return new User("ozizka");
   }

   public User byName(String userName) {
      // TODO: Impl
      return new User("ozizka");
   }

   public List<User> getUsersNameStartsWith(String input) {
      // TODO: Impl
      return users;
   }


}// class
