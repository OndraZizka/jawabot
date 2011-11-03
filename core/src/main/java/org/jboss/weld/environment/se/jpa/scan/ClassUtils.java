package org.jboss.weld.environment.se.jpa.scan;



import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Ondrej Zizka
 */
public class ClassUtils 
{
   private static final Logger log = LoggerFactory.getLogger( ClassUtils.class );
   

   
   /**
    * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
    *
    * @param packageName The base package
    * @return The classes
    * @throws ClassNotFoundException
    * @throws IOException
    */
   @SuppressWarnings( "unchecked" )
   public static List<Class> getClassesFromPackage( String packageName ) throws IOException 
   {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      assert classLoader != null;
      
      String path = packageName.replace( '.', '/' );
      Enumeration<URL> resources = classLoader.getResources( path );
      
      List<Class> classes = new LinkedList();
      
      while( resources.hasMoreElements() ) {
         URL resource = resources.nextElement();
         String fileName = resource.getFile();
         String fileNameDecoded = URLDecoder.decode( fileName, "UTF-8" );
         File dir = new File( fileNameDecoded );
         findClasses( classes, dir, packageName );
      }
      
      return classes;
   }

   /**
    * Recursive method used to find all classes in a given directory tree.
    *
    * @param directory   The base directory.
    * @param packageName The package name for classes found inside the base directory.
    */
   @SuppressWarnings( "unchecked" )
   private static void findClasses( List<Class> classes, File directory, String packageName ) {
      if( !directory.exists() ) {
         return;
      }
      
      File[] files = directory.listFiles();
      for( File file : files ) {
         String fileName = file.getName();
         if( file.isDirectory() ) {
            if( fileName.contains( "." ) )  continue;
            findClasses( classes, file, packageName + "." + fileName );
            return;
         }
                 
         if( !fileName.endsWith( ".class" ) )  continue;
         if( fileName.contains( "$" ) )   continue;
         
         try {
            String className = packageName + "." + fileName.substring( 0, fileName.length() - 6);
            try {
               classes.add( Class.forName(  className ) );
            }
            // Happens e.g. in classes depending on Spring injection which fail if dependency is not satisfied.
            catch( ExceptionInInitializerError e ) {
               classes.add( Class.forName( className, false, Thread.currentThread().getContextClassLoader() ) );
            }
         }
         catch( ClassNotFoundException ex ){
            log.warn( ex.getMessage() );
         }

      }
   }// findClasses()

}// class ClassUtils
