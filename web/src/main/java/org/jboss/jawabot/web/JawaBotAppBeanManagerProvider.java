package org.jboss.jawabot.web;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

/**
 *  
 *  @author Ondrej Zizka
 */
public class JawaBotAppBeanManagerProvider implements org.jboss.seam.solder.beanManager.BeanManagerProvider {

   @Inject private BeanManager beanManager;

   @Override
   public BeanManager getBeanManager() {
      assert this.beanManager != null;
      return this.beanManager;
   }

   @Override
   public int getPrecedence() {
      return 15;
   }
   
}// class

