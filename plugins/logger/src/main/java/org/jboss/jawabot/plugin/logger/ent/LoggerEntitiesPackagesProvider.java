package org.jboss.jawabot.plugin.logger.ent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.jboss.weld.environment.se.jpa.IEntitiesPackagesProvider;

/**
 *  
 *  @author Ondrej Zizka
 */
public class LoggerEntitiesPackagesProvider implements IEntitiesPackagesProvider {
   
   
   @Override
   public Collection<String> getEntityPackages() {
      return Arrays.asList( new String[]{
         "org.jboss.jawabot.plugin.logger.ent",
      });
   }
   
   @Override
   public Collection<Class> getEntityClasses() {
      return Collections.EMPTY_LIST;
   }
   
}// class

