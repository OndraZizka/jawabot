package org.jboss.jawabot;

import cz.dynawest.util.plugin.PluginLoadEx;
import cz.dynawest.util.plugin.PluginUtils;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jboss.jawabot.config.beans.ConfigBean;
import org.jboss.jawabot.ex.JawaBotException;
import org.apache.log4j.Logger;
import org.jboss.jawabot.config.JaxbConfigPersister;
import org.jboss.jawabot.pastebin.PasteBinManager;
import org.jboss.jawabot.usermgr.UserManager;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;


@Singleton
public class JawaBotApp
{
   private static final Logger log = Logger.getLogger(JawaBotApp.class);
   /*static{
      org.apache.log4j.BasicConfigurator.configure();
   }*/
   
   @Inject private BeanManager beanManager;
   
   @Inject private Instance<IModuleHook> moduleHookInstances;
   

   
   // Things shown in the "help" command reply.
   public static final String VERSION = "2.0.0";
   public static final String PROJECT_DOC_URL = "https://docspace.corp.redhat.com/clearspace/docs/DOC-29621";

   

   // JawaBot instance reference.
   private static JawaBot jawaBot;
   public static JawaBot getJawaBot() { return jawaBot; }

   
   // UserManager instance. Read-only, no need to sync.
   private static final UserManager userManager = new UserManager();
   public static UserManager getUserManager() { return userManager; }

   // PasteBin manager.
   private static final PasteBinManager pasteBinManager = new PasteBinManager();
   public static PasteBinManager getPasteBinManager() { return pasteBinManager; }

   
   
   /**
    * Main.
    */
   public static void main(String[] args) throws JawaBotException {

      WeldContainer weld = new Weld().initialize();
      JawaBotApp jawaBotApp = weld.instance().select(JawaBotApp.class).get();
      
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
         this.initAndStartModules(); // TODO: Move to JawaBot.
         this.getJawaBot().waitForShutdown();
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
    *  Initialization of all modules (like IRC and Web).
    */
   private void initAndStartModules() throws JawaBotException {

      
      // For listing of init errors.
      List<Throwable> exs = new ArrayList<Throwable>();
      List<String> errModules = new ArrayList<String>();
      
      //IModuleHook[] moduleHooks = new IModuleHook[moduleHookInstances];
      List<IModuleHook> moduleHooks = new ArrayList();
      for( IModuleHook iModuleHook : moduleHookInstances ) {
         moduleHooks.add(iModuleHook);
      }
      
      // Init
      for( IModuleHook hook : moduleHooks ) {
         try {
            if( null == hook ) continue;
            hook.initModule( getJawaBot(), getJawaBot().getConfig() );
         } catch( Throwable ex ) {
            exs.add( ex );
            errModules.add( hook.getClass().getName() );
         }
      }
      
      // Start
      for( IModuleHook hook : moduleHooks ) {
         try {
            if( null == hook ) continue;
            hook.startModule();
         } catch( Throwable ex ) {
            exs.add( ex );
            errModules.add( hook.getClass().getName() );
         }
      }
      
      throwFormattedExceptionIfNeeded( exs, errModules );
      
   }// initAndStartModules()

   
   /**
    *  Initialization of all modules.
    *  Currently listed statically - IRC and Web.
    *  With a bit of PHP style on top.
    */
   private void initAndStartModules_Old() throws JawaBotException {

      String[] moduleNames = new String[] {
         "org.jboss.jawabot.irc.IrcModuleHook",
         "org.jboss.jawabot.mod.web.WebModuleHook",
      };
      
     
      IModuleHook[] moduleHooks = new IModuleHook[moduleNames.length];

      // For listing of init errors.
      List<Throwable> exs = new ArrayList<Throwable>();
      List<String> errModules = new ArrayList<String>();
      
      // Instantiate
      for (int i = 0; i < moduleNames.length; i++) {
         try {
            moduleHooks[i] = PluginUtils.<IModuleHook>instantiateModule( moduleNames[i] );
         } catch(  PluginLoadEx ex ) {
            exs.add( ex );
            errModules.add( moduleNames[i] );
         }
      }
      
      // Init
      for( IModuleHook hook : moduleHooks ) {
         try {
            if( null == hook ) continue;
            hook.initModule( getJawaBot(), getJawaBot().getConfig() );
         } catch( Throwable ex ) {
            exs.add( ex );
            errModules.add( hook.getClass().getName() );
         }
      }
      
      // Start
      for( IModuleHook hook : moduleHooks ) {
         try {
            if( null == hook ) continue;
            hook.startModule();
         } catch( Throwable ex ) {
            exs.add( ex );
            errModules.add( hook.getClass().getName() );
         }
      }
      
      throwFormattedExceptionIfNeeded( exs, errModules );
      
   }// initAndStartModules_Old()

   
   
   
   /**
    *  Formats a list of exceptions into a readable block.
    *  @param exs
    *  @param errModules
    *  @throws JawaBotException 
    */
   public static void throwFormattedExceptionIfNeeded( List<Throwable> exs, List<String> errModules ) throws JawaBotException 
   {
      if( exs.size() != 0 ){
         StringBuilder sb = new StringBuilder("Some modules couldn't be initialized or started: ")
           .append( StringUtils.join( errModules, ", ") )
           .append("\n");
         for( Throwable ex : exs ) {
            if( ex instanceof PluginLoadEx ){
               PluginLoadEx plex = (PluginLoadEx) ex;
               sb.append("\n  ")
                 .append( plex.getModuleClass() )
                 .append( ": " )
                 .append( ExceptionUtils.getRootCauseMessage(plex) );
            }
            else{
               sb.append("\n")
                 .append( ExceptionUtils.getRootCauseMessage(ex) )
                 .append("\n")
                 .append( StringUtils.join( ExceptionUtils.getRootCauseStackTrace(ex), "\n") );
            }
            sb.append("\n");
         }
         throw new JawaBotException( sb.toString(), null);
      }
   }


   
   
   /*
    *  Initialization of a single module, implemented by given class.
    *  MOVED to PluginUtils.
    */
   /*
   private static IModuleHook instantiateModule( String moduleClass ) throws JawaBotException {
      
      Class<?> cls;
      try {
         cls = Class.forName( moduleClass  );
         IModuleHook hook = (IModuleHook) cls.newInstance();
         return hook;
      } catch( ClassNotFoundException ex ) {
         throw new ModuleNotFoundEx( moduleClass, "Module's class not found.", ex );
      } catch( IllegalAccessException ex ) {
         throw new ModuleNotFoundEx( moduleClass, ex.getMessage() + " " + moduleClass, ex );
      } catch( InstantiationException ex ) {
         throw new ModuleNotFoundEx( moduleClass, ex.getMessage() + " " + moduleClass, ex );
      }
      
   }*/

   
   
   /**
    *  Id's for object lookups.
    */
   public interface ID {
      public static final String JAWABOT    = "JawaBot.jawaBot";
      public static final String RESOURCE_MANAGER    = "JawaBot.resourceManager";
   }
   

}// class
