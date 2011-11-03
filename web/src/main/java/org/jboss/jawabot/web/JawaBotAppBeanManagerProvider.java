package org.jboss.jawabot.web;

import javax.enterprise.inject.spi.BeanManager;
import org.jboss.jawabot.JawaBotApp;

/**
 *  
 *  @author Ondrej Zizka
 */
public class JawaBotAppBeanManagerProvider implements org.jboss.seam.solder.beanManager.BeanManagerProvider {

   @Override
   public BeanManager getBeanManager() {
      return JawaBotApp.beanManager;
   }

   @Override
   public int getPrecedence() {
      return 15;
   }
   
}// class

