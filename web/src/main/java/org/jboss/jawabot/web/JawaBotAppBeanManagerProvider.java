package org.jboss.jawabot.web;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Singleton;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.weld.environment.se.events.ContainerInitialized;

/**
 *  BeanManagerProvider for Seam-Wicket. 
 *  JNDI-based doesn't work, Servlet-based works only with weld-servlet or what.
 *  So I do it either through static field in JawaBotApp 
 *  or through @Observes method and injecting BeanManager in that.
 * 
 *  @author Ondrej Zizka
 */
@Singleton
public class JawaBotAppBeanManagerProvider implements org.jboss.seam.solder.beanManager.BeanManagerProvider {

   // Can't be done through CDI since it's instantiated by ServiceLoader.
   //@Inject
   private static BeanManager beanManager;
   
   
   private void init( @Observes ContainerInitialized ci, BeanManager beanManager ){
      JawaBotAppBeanManagerProvider.beanManager = beanManager;
   }
   
   

   @Override
   public BeanManager getBeanManager() {
      assert beanManager != null;
      return beanManager;
      //return JawaBotApp.getBeanManager();
   }

   @Override
   public int getPrecedence() {
      return 15;
   }
   
}// class

