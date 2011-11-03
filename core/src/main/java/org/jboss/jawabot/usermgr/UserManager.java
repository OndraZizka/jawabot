
package org.jboss.jawabot.usermgr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.jboss.jawabot.state.ent.User;

/**
 *  
 *  @author Ondrej Zizka
 */
public class UserManager {

   private final List<User> users = new ArrayList();

   public UserManager() {
      String[] names = StringUtils.split("ozizka plavice rhusar istudens rsvoboda lpetrovi");
      Arrays.sort(names);
      for( String name : names ) {
         this.users.add( new User( name ) );
      }
   }


   public List<User> getUsers_OrderByName(){
      return this.users;
   }


}// class
