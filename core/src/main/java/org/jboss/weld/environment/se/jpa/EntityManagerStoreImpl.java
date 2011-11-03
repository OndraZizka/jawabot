//package de.laliluna.transactions;
package org.jboss.weld.environment.se.jpa;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Stack;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.hibernate.ejb.Ejb3Configuration;

/**
 * A store for entity managers. It is basically a ThreadLocal which stores the entity manager.
 * The {@link org.jboss.weld.environment.se.jpa.JpaTransactionInterceptor} is expected to register entity manager. The application code
 * can get the current entity manager either by injecting the store or the {@link EntityManagerDelegate}.
 *
 * @author Sebastian Hennebrueder
 */
@ApplicationScoped
public class EntityManagerStoreImpl implements EntityManagerStore {

		private final Logger log = LoggerFactory.getLogger(EntityManagerStoreImpl.class);
		
		private EntityManagerFactory emf;
		
		private ThreadLocal<Stack<EntityManager>> emStackThreadLocal = new ThreadLocal<Stack<EntityManager>>();
      
      @Inject 
		private CdiPluginEntitiesPackagesProvider entPackProv;


		@PostConstruct
      //@Inject  // TODO: Report RFE - Handle wrong param count of a method with @PostConstruct.
		public void init(/*@Observes ContainerInitialized containerInitialized*/   /*CdiPluginEntitiesPackagesProvider entPackProv*/ ) {
            // Hibernate, not JPA
            // new AnnotationConfiguration().addPackage(...)
            
            Ejb3Configuration ejbConf = new Ejb3Configuration();
            /* ejbConf.addProperties( properties ) //add some properties
               ejbConf.addRerousce( "mypath/MyOtherCLass.hbm.xml ) //add an hbm.xml file
               ejbConf.addRerousce( "mypath/orm.xml ) //add an EJB3 deployment descriptor
               ejbConf.configure("/mypath/hibernate.cfg.xml") //add a regular hibernate.cfg.xml*/
            ejbConf.configure("TestPU", null);
            //ejbConf.addPackage("org.jboss.jawabot.irc.ent");
            //ejbConf.addPackage("org.jboss.jawabot.irc.model");
            for( String pack :  entPackProv.getEntityPackages() ){
               log.debug("  Adding entity package to Ejb3Configuration: " + pack);
               ejbConf.addPackage( pack );
            }
            emf = ejbConf.buildEntityManagerFactory(); //Create the entity manager factory
     

				//emf = Persistence.createEntityManagerFactory("TestPU");
		}
		

		@Override
		public EntityManager get() {
				log.debug("Getting the current entity manager");
				
				final Stack<EntityManager> entityManagerStack = emStackThreadLocal.get();
				
				if (entityManagerStack == null || entityManagerStack.isEmpty())
				{
						// If nothing is found, we return null to cause a NullPointer exception in the business code.
						// This leads to a nicer stack trace starting with client code. */
						log.warn("No entity manager was found. "
                          + "Did you forget to mark your method as @"+JpaTransactional.class.getSimpleName()+"? "
                          + "Was the bean loaded through CDI?");

						return null;
				} else {
						return entityManagerStack.peek();
				}
		}

		/**
		 * Creates an entity manager and stores it in a stack. The use of a stack allows to implement
		 * transaction with a 'requires new' behaviour.
		 *
		 * @return the created entity manager
		 */
		@Override
		public EntityManager createAndRegister() {
				log.debug("Creating and registering an entity manager");
				Stack<EntityManager> entityManagerStack = emStackThreadLocal.get();
				if (entityManagerStack == null) {
						entityManagerStack = new Stack<EntityManager>();
						emStackThreadLocal.set(entityManagerStack);
				}

				final EntityManager entityManager = emf.createEntityManager();
				entityManagerStack.push(entityManager);
				return entityManager;
		}

		/**
		 * Removes an entity manager from the thread local stack. It needs to be created using the
		 * {@link #createAndRegister()} method.
		 *
		 * @param entityManager - the entity manager to remove
		 * @throws IllegalStateException in case the entity manager was not found on the stack
		 */
		@Override
		public void unregister(EntityManager entityManager) {
				log.debug("Unregistering an entity manager");
				final Stack<EntityManager> entityManagerStack = emStackThreadLocal.get();
				if (entityManagerStack == null || entityManagerStack.isEmpty()) {
						throw new IllegalStateException("Removing of entity manager failed. Your entity manager was not found.");
				}

				if (entityManagerStack.peek() != entityManager) {
						throw new IllegalStateException("Removing of entity manager failed. Your entity manager was not found.");
				}
				entityManagerStack.pop();
		}
}// class

