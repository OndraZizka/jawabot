package org.jboss.jawabot.irc.ent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.jboss.weld.environment.se.jpa.IEntitiesPackagesProvider;

/**
 *  
 *  @author Ondrej Zizka
 */
public class IrcModuleEntitiesPackagesProvider implements IEntitiesPackagesProvider {
   
   
   @Override
   public Collection<String> getEntityPackages() {
      return Arrays.asList( new String[]{
         IrcModuleEntitiesPackagesProvider.class.getPackage().getName(),
      });
   }
   
   @Override
   public Collection<Class> getEntityClasses() {
      return Collections.EMPTY_LIST;
   }
   
}// class

