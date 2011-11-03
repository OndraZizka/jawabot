package cz.dynawest.util.plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;


/**
 *  Helper code for plug-in management - loading, interfaces, exceptions handling... 
 * 
 *  @author Ondrej Zizka
 */
public class PluginUtils {
   
   
   /**
    *  Initialization of a single module, implemented by given class.
    */
   public static <T> T instantiateModule( String moduleClass ) throws PluginLoadEx {
      
      Class<?> cls;
      try {
         cls = Class.forName( moduleClass  );
         T plugin = (T) cls.newInstance();
         return plugin;
      } catch( ClassNotFoundException ex ) {
         throw new PluginLoadEx( moduleClass, "Module's class not found.", ex );
      } catch( IllegalAccessException ex ) {
         throw new PluginLoadEx( moduleClass, ex.getMessage() + " " + moduleClass, ex );
      } catch( InstantiationException ex ) {
         throw new PluginLoadEx( moduleClass, ex.getMessage() + " " + moduleClass, ex );
      }
      
   }

   
     
   
   /**
    *  Initialization of all modules.
    *  Currently listed statically.
    */
   public static <T extends IPluginLifeCycle> List<T> initAndStartModules( String[] moduleNames, Object initObj ) throws PluginEx {

      //IPluginLifeCycle[] moduleHooks = new IPluginLifeCycle[moduleNames.length];
      List<T> moduleHooks = new ArrayList(moduleNames.length);

      // For listing of init errors.
      List<Throwable> exs = new ArrayList<Throwable>();
      List<String> errModules = new ArrayList<String>();
      
      // Instantiate
      for (int i = 0; i < moduleNames.length; i++) {
         try {
            //moduleHooks[i] = PluginUtils.<IPluginLifeCycle>instantiateModule( moduleNames[i] );
            moduleHooks.add(i, PluginUtils.<T>instantiateModule( moduleNames[i] ) );
         } catch(  PluginLoadEx ex ) {
            exs.add( ex );
            errModules.add( moduleNames[i] );
         }
      }
      
      // Init
      for( IPluginLifeCycle hook : moduleHooks ) {
         try {
            if( null == hook ) continue;
            hook.initModule( initObj );
         } catch( Throwable ex ) {
            exs.add( ex );
            errModules.add( hook.getClass().getName() );
         }
      }
      
      // Start
      for( IPluginLifeCycle hook : moduleHooks ) {
         try {
            if( null == hook ) continue;
            hook.startModule();
         } catch( Throwable ex ) {
            exs.add( ex );
            errModules.add( hook.getClass().getName() );
         }
      }

      //if( exs.size() == 1 )
      //   throw new JawaBotException( exs.get(0).getMessage(), exs.get(0) );
      
      if( exs.size() != 0 ){
         StringBuilder sb = new StringBuilder("Some plugins couldn't be initialized or started: ")
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
         throw new PluginEx( sb.toString() );
      }
      
      //return (T[]) moduleHooks;
      return moduleHooks;
      
   }// initAndStartModules()
   
   
   
   /**
    *  Formats a list of exceptions into a readable block.
    */
   public static <T extends Exception> void throwFormattedExceptionIfNeeded( List<Throwable> exs, List<String> errModules, Class<T> cls ) throws T
   {
      if( exs.size() == 0 )
         return;
      
      
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


      try {
         throw cls.getConstructor( String.class ).newInstance( sb.toString() );
      } catch (InstantiationException ex) {
         throw new RuntimeException("Failed creating "+ cls.getSimpleName()+" due to: "+ex+"\n   It should have contained: ", new Exception(sb.toString()) );
      } catch (IllegalAccessException ex) {
         throw new RuntimeException("Failed creating "+ cls.getSimpleName()+" due to: "+ex+"\n   It should have contained: ", new Exception(sb.toString()) );
      } catch (NoSuchMethodException ex) {
         throw new RuntimeException("Failed creating "+ cls.getSimpleName()+" due to: "+ex+"\n   It should have contained: ", new Exception(sb.toString()) );
      } catch (SecurityException ex) {
         throw new RuntimeException("Failed creating "+ cls.getSimpleName()+" due to: "+ex+"\n   It should have contained: ", new Exception(sb.toString()) );
      } catch (IllegalArgumentException ex) {
         throw new RuntimeException("Failed creating "+ cls.getSimpleName()+" due to: "+ex+"\n   It should have contained: ", new Exception(sb.toString()) );
      } catch (InvocationTargetException ex) {
         throw new RuntimeException("Failed creating "+ cls.getSimpleName()+" due to: "+ex+"\n   It should have contained: ", new Exception(sb.toString()) );
      }
         
   }// throwFormattedExceptionIfNeeded()


   
   
}// class
