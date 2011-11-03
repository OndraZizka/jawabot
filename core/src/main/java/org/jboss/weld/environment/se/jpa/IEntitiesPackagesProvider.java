package org.jboss.weld.environment.se.jpa;

import java.util.Collection;

/**
 *  Implementing class provides a list of packages to search for entities.
 * 
 *  @author Ondrej Zizka
 */
public interface IEntitiesPackagesProvider {

   /**
    *  @returns  A list of packages to search for entities.
    */
   public Collection<String> getEntityPackages();
   
   public Collection<Class> getEntityClasses();
   
}// class

