package org.jboss.weld.environment.se.jpa;

import java.util.Collection;

/**
 *  Provides a list of packages to search for entities.
 * 
 *  @author Ondrej Zizka
 */
public interface EntitiesPackagesProvider {

   public Collection<String> getEntityPackages();
   
}// class

