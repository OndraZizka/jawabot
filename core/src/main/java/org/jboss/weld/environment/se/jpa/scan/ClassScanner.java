package org.jboss.weld.environment.se.jpa.scan;


import java.io.*;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.*;
import java.util.jar.*;


/**
 *   http://stackoverflow.com/questions/1456930/read-all-classes-from-java-package-in-classpath
 *   Aaargh... Too ugly to use. TODO: Dump.
 * 
 *   I've sanitized this class a bit, but still it has one major problem:
 *   For every invocation of different packages, it would scan the same jar again and again.
 
 *   TODO: Use Bill Burke's Scannotation.
 */
@Deprecated
public class ClassScanner {
      
   /**
    * Returns an array of class in the same package as the the SeedClass
    * 
    * @param classFilter   - The super class of the desired classes (null if no filtering needed)
    * 
    * @return                - The array of matched classes, null if there is a problem.
    * 
    * @author The rest       - Nawaman  http://nawaman.net
    * @author Package as Dir - Jon Peck http://jonpeck.com
    *                              (adapted from http://www.javaworld.com/javaworld/javatips/jw-javatip113.html)
    */
   @SuppressWarnings( "unchecked" )
   public static <T> List<Class<? extends T>> discoverClasses( Class<?> seedClass, Class<T> classFilter) throws URISyntaxException, IOException 
   {
      assert classFilter != null;
      
      PkgInfo pkgInfo = PkgInfo.fromClass( seedClass );
      List<String> classNames = scanPackage( pkgInfo );
      return filterClassesByType( classNames, classFilter );
   }
   
   /**
    * 
    */
   private static <T> List<Class<? extends T>> filterClassesByType( List<String> classNames, Class<T> classFilter ){
      List<Class<? extends T>> classes = new LinkedList();
      
      // For each className found in package...
      for( final String clsName : classNames ) {

         // Get the class and filter it
         Class<?> cls = null;
         try {
            cls = Class.forName( clsName );
         } catch( Exception e ) { // ClassNotFoundException, NoClassDefFoundError
            continue;
         }

         if( classFilter.isAssignableFrom( cls ) )
            classes.add( cls.asSubclass(classFilter) );
      }
      return classes;
   }
   
   
   /**
    * 
    */
   private static List<String> scanPackage( PkgInfo pkgInfo ) throws IOException {

      if( pkgInfo.path.isDirectory() )
         return ClassScanner.scanDirectory( pkgInfo );
      
      if( !pkgInfo.path.isFile() )
         throw new IllegalStateException("  Package "+pkgInfo.path+" is not in a directory nor .jar file.");
         
      if( ! pkgInfo.path.getPath().endsWith(".jar") )
         throw new IllegalStateException("  Package "+pkgInfo.path+" is in a file which is not a .jar.");
      
      return ClassScanner.scanJarFile( pkgInfo );
   }
   
   
   /**
    * 
    */
   private static List<String> scanJarFile( PkgInfo pkgInfo ) throws IOException {

      List<String> classNames = new LinkedList();
      
      JarFile jarFile = new JarFile( pkgInfo.path );
      Enumeration<JarEntry> jarEntries = jarFile.entries();

      while( jarEntries.hasMoreElements() ) {
         JarEntry jarEntry = jarEntries.nextElement();
         String name = jarEntry.getName();
         if( ! name.endsWith( ".class" ) )              continue;
         if( ! name.startsWith( pkgInfo.nameAsPath ) )  continue;
         
         String fullClassName = classFilePathToClassName( name );
         classNames.add( fullClassName );
      }
      
      jarFile.close();
      return classNames;
   }

   /*
    *   org/jboss/jawabot/plugin/pastebin/MemoryPasteBinManager.class + org.jboss.jawabot.plugin.pastebin
    *   org.jboss.jawabot.plugin.pastebin.MemoryPasteBinManager
    */   
   private static String classFilePathToClassName(String name) {
      return name.substring( 0, name.length() - 6 ).replace( File.separatorChar, '.' );
   }
   
   

   
   /**
    * 
    */
   private static List<String> scanDirectory( PkgInfo pPkgInfo ) {

      final List<String> classNames = new LinkedList();
      
      String dirPath = pPkgInfo.path.getAbsolutePath() + '/' + pPkgInfo.name.replace( '.', '/' ); // TODO: asPath?
      File pkgDir = new File( dirPath );
      if( !pkgDir.exists() || !pkgDir.isDirectory() )
         return classNames;

      scanDirectory_Recursive( pkgDir.getAbsolutePath(), pPkgInfo.name, pkgDir, classNames );
      return classNames;
   }
   

   /**
    * 
    */
   private static void scanDirectory_Recursive( final String absPkgPath, String packageName, File packageDir, List<String> classNames) 
   {
      for( File dirEntry : packageDir.listFiles() ) {
         
         // Directory.
         if( dirEntry.isDirectory() ) {
            scanDirectory_Recursive( absPkgPath, packageName, dirEntry, classNames );
            continue;
         }

         if( ! dirEntry.getPath().endsWith( ".class" ) )
            continue;
         
         String fileName = dirEntry.getAbsolutePath().substring( absPkgPath.length() + 1 );

         String simpleName = fileName.substring( 0, fileName.length() - 6 ).replace( File.separatorChar, '.' );
         String fullClassName = packageName + "." + simpleName;
         classNames.add( fullClassName );
      }
   }

   
   
   
   /**
    *   main()
    */
   public static void main(String... pArgs) throws URISyntaxException, IOException {
      
      Class<?> seedClass = null;
      try { 
         seedClass = Class.forName( pArgs[0] );
      } catch( Exception ex ) {
         seedClass = ClassScanner.class;
      }
     
      List<Class<? extends Object>> classes = discoverClasses( seedClass, Object.class );
      for( Class cls : classes ) {
         System.out.println( "\t" + cls );
      }
   }
   
}



/**
 * 
 */
class PkgInfo {

   final File path;
   final String name;
   final String nameAsPath;
   
   PkgInfo( File pPkgPath, String pPkgName, String pPkgAsPath) {
      this.path = pPkgPath;
      this.name = pPkgName;
      this.nameAsPath = pPkgAsPath;
   }

   
   /**
    *   Basic pre-cached info.
    */
   public static PkgInfo fromClass( Class<?> cls ) throws URISyntaxException {
      
      String name = cls.getPackage().getName();
      String nameSlash = name.replace( '.', '/' ) + '/';

      CodeSource codeSource = cls.getProtectionDomain().getCodeSource();
      if( codeSource == null )
         throw new UnsupportedOperationException( "Class does not have bytecode source (maybe a JDK class?): " + cls.toString());

      File pkgPath = new File( codeSource.getLocation().toURI() );

      return new PkgInfo( pkgPath, name, nameSlash );
   }
   
}
