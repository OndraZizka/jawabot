package org.jboss.jawabot;

import cz.dynawest.util.plugin.cdi.CdiPluginUtils;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.commons.lang.StringUtils;
import org.jboss.jawabot.config.beans.ConfigBean;
import org.jboss.jawabot.ex.JawaBotException;
import org.slf4j.Logger; import org.slf4j.LoggerFactory; 
import org.jboss.jawabot.config.JaxbConfigPersister;
import org.jboss.jawabot.usermgr.UserManager;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.jboss.weld.environment.se.jpa.EntityManagerStore;


/**
 *  Application class - entry point of the application.
 *  Used statically.
 *  Bootstraps CDI.
 *  Creates JawaBot, initializes it, configures it,
 *  
 * @author ondra
 */
@Singleton
public class JawaBotApp
{
    private static final Logger log = LoggerFactory.getLogger(JawaBotApp.class);

    @Inject private Instance<IModuleHook> moduleHookInstances;
    @Inject private EntityManagerStore emf; // To have it created at the very start, we would have to call em.get().
   

   
    // Find VERSION from a package.
    public static final String VERSION;
    static{
        String version = null;
        try { version = JawaBotApp.class.getPackage().getImplementationVersion(); } catch( Throwable ex ){ }
        if( version == null ) version = "";
        VERSION = StringUtils.defaultString( version, "" );
     }
   
   
    //public static final String PROJECT_DOC_URL = "https://docspace.corp.redhat.com/clearspace/docs/DOC-29621";
    public static final String PROJECT_DOC_URL = "http://code.google.com/p/jawabot/";

   

    // JawaBot instance reference.
    private static JawaBot jawaBot;
    @Produces public static JawaBot getJawaBot() {
        return jawaBot;
    }

   
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
    *  Reads the options, creates jawabot, applies options on it, 
    *  initializes the plugins, and last, waits for JawaBot to notify shutting down.
    */
   public void run(String[] args) throws JawaBotException {
      log.debug( JawaBotApp.class.getSimpleName() + "#main() start.");
      
      Options options = new Options().applySysProps().applyAppParams(args).validate();

      try {
         ConfigBean cb = new JaxbConfigPersister( options.getConfigFile() ).load();
         JawaBotApp.jawaBot = JawaBot.create( cb );
         
         // TODO: Move to JawaBot.
         CdiPluginUtils.initAndStartPlugins( this.moduleHookInstances, JawaBotApp.jawaBot, JawaBotException.class);
         JawaBotApp.jawaBot.waitForShutdown();
      } catch ( JawaBotException ex ) {
         log.error( "Error during JawaBot initialization", ex );
      }

      log.debug( JawaBotApp.class.getSimpleName() + "#main() end.");
   }
   

   
   
   
   
   /**
    *  This is here for JawaBotAppBeanManagerProvider.
    */
   public static BeanManager getBeanManager() { return beanManager; }
   private static BeanManager beanManager;
   
  

}// class
