package org.jboss.jawabot;

import cz.dynawest.util.plugin.PluginLoadEx;
import cz.dynawest.util.plugin.PluginUtils;
import cz.dynawest.util.plugin.cdi.CdiPluginUtils;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jboss.jawabot.config.beans.ConfigBean;
import org.jboss.jawabot.ex.JawaBotException;
import org.apache.log4j.Logger;
import org.jboss.jawabot.config.JaxbConfigPersister;
import org.jboss.jawabot.usermgr.UserManager;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.se.jpa.EntityManagerStore;


@Singleton
public class JawaBotApp
{
   private static final Logger log = Logger.getLogger(JawaBotApp.class);
   /*static{
      org.apache.log4j.BasicConfigurator.configure();
   }*/
   
   //@Inject private BeanManager beanManager;
   
   @Inject private Instance<IModuleHook> moduleHookInstances;
   @Inject private EntityManagerStore emf; // To have it created at the very start.
   

   
   // Things shown in the "help" command reply.
   public static final String VERSION = "2.0.0";
   public static final String PROJECT_DOC_URL = "https://docspace.corp.redhat.com/clearspace/docs/DOC-29621";

   

   // JawaBot instance reference.
   private static JawaBot jawaBot;
   @Produces public static JawaBot getJawaBot() { return jawaBot; }

   
   // UserManager instance. Read-only, no need to sync.
   private static final UserManager userManager = new UserManager();
   public static UserManager getUserManager() { return userManager; }

   // PasteBin manager.
   //private static final MemoryPasteBinManager pasteBinManager = new MemoryPasteBinManager();
   //public static IPasteBinManager getPasteBinManager() { return pasteBinManager; }

   
   
   /**
    * Instantiates JawaBotApp through CDI/Weld and calls it's run().
    */
   public static void main(String[] args) throws JawaBotException {

      WeldContainer weld = new Weld().initialize();
      JawaBotApp jawaBotApp = weld.instance().select(JawaBotApp.class).get();
      weld.getBeanManager().getContext(ApplicationScoped.class);
      
      jawaBotApp.run(args);
   }
   

   /**
    * Run.
    */
   public void run(String[] args) throws JawaBotException {

      log.debug( JawaBotApp.class.getSimpleName() + "#main() start.");

      try {
         String configFilePath = System.getProperty("config", "JawaBotConfig-debug.xml");
         this.init( configFilePath );
         //this.initAndStartModules(); // TODO: Move to JawaBot.
         CdiPluginUtils.initAndStartPlugins( this.moduleHookInstances, JawaBotApp.getJawaBot(), JawaBotException.class);
         JawaBotApp.getJawaBot().waitForShutdown();
      } catch ( JawaBotException ex ) {
         ex.printStackTrace();
      }

      log.debug( JawaBotApp.class.getSimpleName() + "#main() end.");
   }
   
   
   /**
    *  Init module instances acquired through CDI.
    */
   private void init( String configFilePathString ) throws JawaBotException {
      ConfigBean cb = new JaxbConfigPersister(configFilePathString).load();
      JawaBotApp.jawaBot = JawaBot.create( cb );
   }

   
   
   /**
    *  Id's for object lookups.
    */
   public interface ID {
      public static final String JAWABOT    = "JawaBot.jawaBot";
      public static final String RESOURCE_MANAGER    = "JawaBot.resourceManager";
   }
   

}// class
