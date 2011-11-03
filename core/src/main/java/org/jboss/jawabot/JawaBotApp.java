package org.jboss.jawabot;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jboss.jawabot.config.beans.ConfigBean;
import org.jboss.jawabot.ex.JawaBotException;
import org.apache.log4j.Logger;
import org.jboss.jawabot.config.JaxbConfigPersister;
import org.jboss.jawabot.ex.ModuleNotFoundEx;
import org.jboss.jawabot.pastebin.PasteBinManager;
import org.jboss.jawabot.usermgr.UserManager;

public class JawaBotApp
{
   private static final Logger log = Logger.getLogger(JawaBotApp.class);
   /*static{
      org.apache.log4j.BasicConfigurator.configure();
   }*/

   
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

      log.debug( JawaBotApp.class.getSimpleName() + "#main() start.");

      try {
         init();
         initAndStartModules(); // TODO: Move to JawaBot.
         getJawaBot().waitForShutdown();
      } catch ( JawaBotException ex ) {
         ex.printStackTrace();
      }

      log.debug( JawaBotApp.class.getSimpleName() + "#main() end.");
   }
   
   
   /**
    *  Init.
    */
   private static void init() throws JawaBotException {
      String configFilePath = System.getProperty("config", "JawaBotConfig-debug.xml");
      ConfigBean cb = new JaxbConfigPersister(configFilePath).load();
      JawaBotApp.jawaBot = JawaBot.create( cb );
   }

   
   
   /**
    *  Initialization of all modules.
    *  Currently listed statically - IRC and Web.
    *  With a bit of PHP style on top.
    */
   private static void initAndStartModules() throws JawaBotException {

      String[] moduleNames = new String[] {
         "org.jboss.jawabot.irc.IrcModuleHook",
         "org.jboss.jawabot.mod.web.WebModuleHook",
      };
      
      IModuleHook[] moduleHooks = new IModuleHook[moduleNames.length];

      // For listing of init errors.
      List<Throwable> exs = new ArrayList<Throwable>();
      List<String> errMods = new ArrayList<String>();
      
      // Instantiate
      for (int i = 0; i < moduleNames.length; i++) {
         try {
            moduleHooks[i] = instantiateModule( moduleNames[i] );
         } catch( JawaBotException ex ) {
            exs.add( ex );
            errMods.add( moduleNames[i] );
         }
      }
      
      // Init
      for( IModuleHook hook : moduleHooks ) {
         try {
            if( null == hook ) continue;
            hook.initModule( getJawaBot(), getJawaBot().getConfig() );
         } catch( Throwable ex ) {
            exs.add( ex );
            errMods.add( hook.getClass().getName() );
         }
      }
      
      // Start
      for( IModuleHook hook : moduleHooks ) {
         try {
            if( null == hook ) continue;
            hook.startModule();
         } catch( Throwable ex ) {
            exs.add( ex );
            errMods.add( hook.getClass().getName() );
         }
      }
      
      //if( exs.size() == 1 )
      //   throw new JawaBotException( exs.get(0).getMessage(), exs.get(0) );
      
      if( exs.size() != 0 ){
         StringBuilder sb = new StringBuilder("Multiple module's couldn't be initialized or started: ")
           .append( StringUtils.join( errMods, ", ") )
           .append("\n");
         for( Throwable ex : exs ) {
            if( ex instanceof ModuleNotFoundEx ){
               ModuleNotFoundEx mex = (ModuleNotFoundEx) ex;
               sb.append("\n  ")
                 .append( mex.getModuleClass() )
                 .append( ": " )
                 .append( ExceptionUtils.getRootCauseMessage(mex) );
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
      
   }// initAndStartModules()

   
   
   /**
    *  Initialization of a single module, implemented by given class.
    */
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
      
   }

   
   
   /**
    *  Id's for object lookups.
    */
   public interface ID {
      public static final String JAWABOT    = "JawaBot.jawaBot";
      public static final String RESOURCE_MANAGER    = "JawaBot.resourceManager";
   }
   

}// class
