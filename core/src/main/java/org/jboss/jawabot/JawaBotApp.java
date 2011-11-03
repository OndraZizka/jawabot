package org.jboss.jawabot;

import cz.dynawest.util.plugin.cdi.CdiPluginUtils;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jboss.jawabot.config.beans.ConfigBean;
import org.jboss.jawabot.ex.JawaBotException;
import org.slf4j.Logger; import org.slf4j.LoggerFactory; 
import org.jboss.jawabot.config.JaxbConfigPersister;
import org.jboss.jawabot.usermgr.UserManager;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.jboss.weld.environment.se.jpa.EntityManagerStore;


@Singleton
public class JawaBotApp
{
   private static final Logger log = LoggerFactory.getLogger(JawaBotApp.class);

   @Inject private Instance<IModuleHook> moduleHookInstances;
   @Inject private EntityManagerStore emf; // To have it created at the very start.
   

   
   // Things shown in the "help" command reply.
   public static final String VERSION = "2.0.0-SNAPSHOT";
   //public static final String PROJECT_DOC_URL = "https://docspace.corp.redhat.com/clearspace/docs/DOC-29621";
   public static final String PROJECT_DOC_URL = "http://code.google.com/p/jawabot/";

   

   // JawaBot instance reference.
   private static JawaBot jawaBot;
   @Produces public static JawaBot getJawaBot() { return jawaBot; }

   
   // UserManager instance. Read-only, no need to sync.
   private static final UserManager userManager = new UserManager();
   public static UserManager getUserManager() { return userManager; }


   
   
   /**
    * Instantiates JawaBotApp through CDI/Weld and calls it's run().
    */
   public static void main(String[] args) throws JawaBotException {

      WeldContainer weld = new Weld().initialize();
      JawaBotApp jawaBotApp = weld.instance().select(JawaBotApp.class).get();
      JawaBotApp.beanManager = weld.getBeanManager();
      weld.event().select( ContainerInitialized.class ).fire( new ContainerInitialized() );
      
      jawaBotApp.run(args);
   }


   /**
    * Run.
    */
   public void run(String[] args) throws JawaBotException {
      log.debug( JawaBotApp.class.getSimpleName() + "#main() start.");
      
      Options options = new Options().applySysProps().applyAppParams(args).validate();

      try {
         this.init( options.getConfigFile() );
         
         // TODO: Move to JawaBot.
         CdiPluginUtils.initAndStartPlugins( this.moduleHookInstances, JawaBotApp.getJawaBot(), JawaBotException.class);
         JawaBotApp.getJawaBot().waitForShutdown();
      } catch ( JawaBotException ex ) {
         log.error( "Error during JawaBot initialization", ex );
      }

      log.debug( JawaBotApp.class.getSimpleName() + "#main() end.");
   }
   
   
   /**
    *  Init module instances acquired through CDI.
    */
   private void init( String configFile ) throws JawaBotException {
      ConfigBean cb = new JaxbConfigPersister( configFile ).load();
      JawaBotApp.jawaBot = JawaBot.create( cb );
   }


   /**
    *  This is here for JawaBotAppBeanManagerProvider.
    */
   public static BeanManager getBeanManager() { return beanManager; }
   private static BeanManager beanManager;
   
      

}// class
