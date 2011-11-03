//package de.laliluna.transactions;
package org.jboss.weld.environment.se.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

/**
 * Now, we can create a transaction interceptor. It will start a transaction before a transactional method is called and commit it after the method was executed.
 * In addition it will register an entity manager in the EntityManagerStore and unregister it afterwards. 
 * Finally, it will roll back transactions in case of an exception.
 * 
 * The annotations @Interceptor and @Transactional tell Weld that every time it finds a @Transactional annotation it needs to call the interceptor.
 * 
 * 
 * A simple transaction interceptor which registers an entity manager in a ThreadLocal and unregisters after the
 * method was called.
 * It does not support any kind of context propagation. If a transactional method calls another's bean transactional
 * method a new entity manager is created and added to the stack.
 * 
 * 
 * TODO:  Design note:  Either EntityManagerStore's stuff should probably be right in this interceptor,
 *                      or the transaction should also be managed there.
 *                      How it's done now spends too much code on querying the store.
 *
 * @author Sebastian Hennebrueder
 * @author Ondrej Zizka
 */
@Interceptor
@JpaTransactional
public class JpaTransactionInterceptor {
		private final Logger log = LoggerFactory.getLogger( JpaTransactionInterceptor.class );

		@Inject
      private EntityManagerStore entityManagerStore;

		@AroundInvoke
		public Object runInTransaction( InvocationContext invocationContext ) throws Exception {

            String method = invocationContext.getMethod().getName()  + "()"; // Debug
            log.debug(" ::: In " + JpaTransactionInterceptor.class.getSimpleName() 
                    +" - @AroundInvoke for " + method + " " + invocationContext.getTarget().getClass().getSimpleName()
            );
            
            // Transaction type - REQUIRED or REQUIRES_NEW ?
            boolean wantsNew = invocationContext.getMethod().getAnnotation(JpaTransactional.class).value() == JpaTransactional.Type.REQUIRES_NEW;
            boolean needsNew = wantsNew || ! this.entityManagerStore.has();
            
				// Create / get EM.
				//EntityManager em = this.entityManagerStore.createAndRegister();
            EntityManager em = this.entityManagerStore.getOrCreateAndRegister( needsNew );

				Object result = null;
				try {
                  if( needsNew || ! em.getTransaction().isActive() ){
                     log.debug("  Starting transaction...");
                     em.getTransaction().begin();
                  }
                  
                  log.debug("     >>> Invoking the intercepted method "  + method + "...");
						result = invocationContext.proceed();
                  log.debug("     <<< Back from invocation of " + method);
                  
                  if( needsNew ){
                     log.debug("     Committing transaction...");
                     em.getTransaction().commit();
                  }
				}
				catch( Exception ex ) {
						try {
								if( em.getTransaction().isActive() ) {
										log.info("Rolled back transaction because of: " + ex);
										em.getTransaction().rollback();
								}
						}
						catch( PersistenceException ex2 ) {
								log.warn("Rollback of transaction failed: " + ex2);
						}
						throw ex;
				}
				finally {
						if( em != null ) {
                        log.debug("Unregistering and closing EntityManager " + em);
                        this.entityManagerStore.unregister(em);
                        if( needsNew )
                           em.close();
						}
				}

				return result;
		}
}// class
