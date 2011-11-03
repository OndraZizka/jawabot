package org.jboss.weld.environment.se.jpa;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 *  This one takes other implementations from CDI and aggregates packages given by them. 
 * 
 *  @author Ondrej Zizka
 */
public class CdiPluginEntitiesPackagesProvider implements EntitiesPackagesProvider {
   
   @Inject Instance<EntitiesPackagesProvider> epps;

   @Override
   public Collection<String> getEntityPackages() 
   {
      Set<String> packages = new HashSet<String>();
      for( EntitiesPackagesProvider epp : epps ){
         // Prevent recursion or circularity.
         if( epp == this || epp instanceof CdiPluginEntitiesPackagesProvider )
            continue;
         packages.addAll( epp.getEntityPackages() );
      }
      return packages;
   }

   @Override
   public Collection<Class> getEntityClasses() 
   {
      Set<Class> classes = new HashSet<Class>();
      for( EntitiesPackagesProvider epp : epps ){
         // Prevent recursion or circularity.
         if( epp == this || epp instanceof CdiPluginEntitiesPackagesProvider )
            continue;
         classes.addAll( epp.getEntityClasses() );
      }
      return classes;
   }
   
}// class
