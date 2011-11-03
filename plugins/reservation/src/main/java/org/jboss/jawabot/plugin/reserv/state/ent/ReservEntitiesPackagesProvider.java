package org.jboss.jawabot.plugin.reserv.state.ent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.jboss.weld.environment.se.jpa.IEntitiesPackagesProvider;

/**
 *  
 *  @author Ondrej Zizka
 */
public class ReservEntitiesPackagesProvider implements IEntitiesPackagesProvider {
   
   
   @Override
   public Collection<String> getEntityPackages() {
      return Arrays.asList( new String[]{
         Reservation.class.getPackage().getName(),
         Resource.class.getPackage().getName(),
         ResourceInGroup.class.getPackage().getName(),
      });
   }

   @Override
   public Collection<Class> getEntityClasses() {
      return Collections.EMPTY_LIST;
   }

   
}// class

