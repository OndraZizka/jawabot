//package de.laliluna.transactions;
package org.jboss.weld.environment.se.jpa;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Stack;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.Entity;
import org.hibernate.ejb.Ejb3Configuration;
import org.jboss.weld.environment.se.jpa.scan.ClassScanner;

/**
 * A store for entity managers. It is basically a ThreadLocal which stores the entity manager.
 * The {@link org.jboss.weld.environment.se.jpa.JpaTransactionInterceptor} is expected to register entity manager. The application code
 * can get the current entity manager either by injecting the store or the {@link EntityManagerDelegate}.
 * 
 * 
 * 
 1. Bill Burke has written a nice article about this: http://bill.burkecentral.com/2008/01/14/scanning-java-annotations-at-runtime/

 2. Hibernate has this already written:
     * org.hibernate.ejb.packaging.Scanner
     * org.hibernate.ejb.packaging.NativeScanner

3. CDI might solve this, but don't know - haven't investigated fully yet:
   http://docs.jboss.org/weld/reference/latest/en-US/html_single/#lookup

      @Inject Instance<MyClass>

   Also for annotations:

      abstract class MyAnnotationQualifier
      extends AnnotationLiteral<Entity> implements Entity {}
 
 * @author Sebastian Hennebrueder
 */
@ApplicationScoped
public class EntityManagerStoreImpl implements EntityManagerStore
{
		private static final Logger log = LoggerFactory.getLogger(EntityManagerStoreImpl.class);
      
		
		private EntityManagerFactory emf;
		
		private ThreadLocal<Stack<EntityManager>> emStackThreadLocal = new ThreadLocal<Stack<EntityManager>>();
      
      @Inject 
		private CdiPluginEntitiesPackagesProvider entPackProv;


		@PostConstruct
      //@Inject  // TODO: Report RFE - Handle wrong param count of a method with @PostConstruct.
		public void init(/*@Observes ContainerInitialized containerInitialized*/   /*CdiPluginEntitiesPackagesProvider entPackProv*/ )
      {
            log.info("================================================================================");
            log.info("==   Persistence initialization  start   =======================================");
            log.info("================================================================================");
            
            // Old simple way.
				//emf = Persistence.createEntityManagerFactory("TestPU");

            // Hibernate, not JPA
            // new AnnotationConfiguration().addPackage(...)
         
            // Will be deprecated in 4.0 in favor of EntityManagerFactoryBuilder - see HHH-6159
            Ejb3Configuration ejbConf = new Ejb3Configuration();
           
            ejbConf.configure("TestPU", null); // TODO: Externalize or use first PU from  persistence.xml.
            
            // Scan packages.
            // https://hibernate.onjira.com/browse/EJB-252 and HHH-6466
            for( String pack :  entPackProv.getEntityPackages() ){
               log.debug("  Adding entities to Ejb3Configuration from package: " + pack);
               try {
                  for( Class cls : getEntityClassesFromPackage( pack ) ) {
                     if( null == cls.getAnnotation( Entity.class ) ) continue;
                     log.debug("    * " + cls.getName());
                     ejbConf.addAnnotatedClass( cls );
                  }
               } catch( ClassNotFoundException ex ) {
                  log.error( "  Package not found, will probably cause 'Unknown entity': " + pack );
               }
            }
            
            // Concrete classes. TODO.
            for( Class cls : entPackProv.getEntityClasses() ){
               log.debug("  Adding entity class to Ejb3Configuration: " + cls.getName() );
               try {
                  ejbConf.addAnnotatedClass( cls );
               } catch( Throwable ex ) {
                  log.error("Failed loading entity class: " + ex, ex);
               }
            }
            
            
            this.emf = ejbConf.buildEntityManagerFactory(); //Create the entity manager factory
            
            log.info("================================================================================");
            log.info("==   Persistence initialization  end  ==========================================");
            log.info("================================================================================");
		}
		
      
      /**
       *   Returns true if this thread already has some EM on stack.
       *   Server the purpose of @JpaTransactional(REQUIRED) vs. REQUIRES_NEW
       */
      public boolean has(){
         Stack<EntityManager> entityManagerStack = this.emStackThreadLocal.get();
         return ! ( entityManagerStack == null || entityManagerStack.isEmpty() );
      }

      /**
       *   This method is called from the EntityManagerDelegate.
       */
		@Override
		public EntityManager get() {
				Stack<EntityManager> emStack = this.emStackThreadLocal.get();
				if( emStack == null || emStack.isEmpty() )
				{
						// If nothing is found, we return null to cause a NullPointer exception in the business code.
						// This leads to a nicer stack trace starting with client code. */
						log.warn("No entity manager was found. "
                          + "Did you forget to mark your method as @"+JpaTransactional.class.getSimpleName()+"? "
                          + "Was the bean loaded through CDI?");
						return null;
				} else {
      				log.debug("  Getting the current EntityManager. Stack: " + emStack.size());
						return emStack.peek();
				}
		}

		/**
		 * Creates an entity manager and stores it in a stack. The use of a stack allows to implement
		 * transaction with a "requires new" behavior.
		 *
		 * @return the created entity manager
		 */
      @Override
      public EntityManager getOrCreateAndRegister( boolean requiresNew ) {
            Stack<EntityManager> emStack = this.getThreadLocalStack();
            EntityManager em;
            if( requiresNew || emStack.isEmpty() ){
               log.debug("  +++ Creating and registering a new EntityManager.");
               em = this.emf.createEntityManager();
	         }
            else {
               log.debug("  === Re-using current EntityManager." );
               em = emStack.peek();
            }
            emStack.push(em); // Add it once more - we will not care in unregister().
            return em;
      }
      

		/**
		 * Removes an entity manager from the thread local stack. It needs to be created using the
		 * {@link #createAndRegister()} or {@link #getOrCreateAndRegister(boolean)} method.
		 *
		 * @param em - the entity manager to remove
		 * @throws IllegalStateException in case the entity manager was not found on the stack.
		 */
		@Override
		public void unregister( EntityManager em )
      {
				final Stack<EntityManager> emStack = this.emStackThreadLocal.get();
				if( emStack == null || emStack.isEmpty()) {
						throw new IllegalStateException("Removing of entity manager failed - no EntityManager was on stack.");
				}

				log.debug("  --- Unregistering an entity manager; stack before: " + emStack.size() );
				if( emStack.peek() != em ) {
						throw new IllegalStateException("Removing of entity manager failed - other EntityManager was on top of stack.");
				}
				emStack.pop();
		}

      
      
      
      /**
       *  Util class, which encapsulates how we actually search for entity classes.
       *  @param pack
       *  @return 
       */
      private static List<Class<? extends Object>> getEntityClassesFromPackage(String pack) throws ClassNotFoundException 
      {
            Class seed = Class.forName( pack + ".package-info" );
            
            List<Class<? extends Object>> classes = Collections.EMPTY_LIST;
            try {
               classes = ClassScanner.discoverClasses( seed, Object.class );
            }  catch( URISyntaxException ex ) {
               log.error( "  Error when scanning package "+pack+" classes: " + ex, ex );
            } catch( IOException ex ) {
               log.error( "  Error when scanning package "+pack+" classes: " + ex, ex );
            }
            return classes;
      }

      
      
      /**
       * @returns  Stack of this thread; never null.
       */
      private Stack<EntityManager> getThreadLocalStack() {
				Stack<EntityManager> emStack = this.emStackThreadLocal.get();
				if( emStack == null ) {
						emStack = new Stack<EntityManager>();
						this.emStackThreadLocal.set(emStack);
				}
            return emStack;
      }

      
      
}// class

