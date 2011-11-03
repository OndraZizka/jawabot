package org.jboss.weld.environment.se.jpa.scan;



import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.jar.*;
import java.util.regex.*;



/**
 *   http://stackoverflow.com/questions/1456930/read-all-classes-from-java-package-in-classpath
 *   Aaargh... Too ugly to use. TODO: Dump.
 */
@Deprecated
public class ClassScanner {
   
   
   
   /**
    * Returns an array of class in the same package as the the SeedClass
    * 
    * @param nameFilter     - Regular expression to match the desired classes' name (nullif no filtering needed)
    * @param classFilter   - The super class of the desired classes (null if no filtering needed)
    * 
    * @return                - The array of matched classes, null if there is a problem.
    * 
    * @author The rest       - Nawaman  http://nawaman.net
    * @author Package as Dir - Jon Peck http://jonpeck.com
    *                              (adapted from http://www.javaworld.com/javaworld/javatips/jw-javatip113.html)
    */
   @SuppressWarnings( "unchecked" )
   public static <T> List<Class<? extends T>> discoverClasses( Class<?> seedClass, String nameFilter, Class<T> classFilter) throws URISyntaxException, IOException 
   {
      assert classFilter != null;
      
      List<Class<? extends T>> classes = new LinkedList();

      PkgInfo pkgInfo = createPackageInfo( seedClass );
      List<String> classNames = discoverClassNames_inPackage( pkgInfo );
      
      Pattern clsNamePat = (nameFilter == null) ? null : Pattern.compile( nameFilter );

      
      // For each className found in package...
      for( final String clsName : classNames ) {

         if( (clsNamePat != null) && !clsNamePat.matcher( clsName ).matches() ) {
            continue;
         }

         // Get the class and filter it
         Class<?> cls = null;
         try {
            cls = Class.forName( clsName );
         } catch( ClassNotFoundException e ) {
            continue;
         } catch( NoClassDefFoundError e ) {
            continue;
         }

         if( !classFilter.isAssignableFrom( cls ) ) {
            continue;
         }
         
         classes.add( cls.asSubclass(classFilter) );
      }

      //Collections.sort( classes, ClassComparator.INSTANCE ); // Makes no sense here

      return classes;
   }
   

   
   
   /**
    * 
    */
   private static String getClassName_afterPackageAsPath( final String fileName, final String pkgAsPath)
   {
      String cname = fileName.substring( 0, fileName.length() - 6 ).replace( '/', '.' ).replace( '\\', '.' );
      String CName_AfterPackageAsPath = cname.substring( pkgAsPath.length() );
      return CName_AfterPackageAsPath;
   }

   /**
    *   @returns  null if the file does not end with .class or it's path does not match given package path.
    */
   private static String getClassName_ofPackageAsPath( final String filePath, final String pkgAsPath ) { 
      if( ! filePath.endsWith( ".class" ) ) 
         return null;

      if( ! filePath.startsWith( pkgAsPath ) )
         return null;

      return ClassScanner.getClassName_afterPackageAsPath( filePath, pkgAsPath );
   }

   /**
    * 
    */
   static private File getPackageFile( String pkgName, File pkgPath ) {

      final String dirPath = pkgPath.getAbsoluteFile().toString() + '/' + pkgName.replace( '.', '/' );
      final File pkgDir = new File( dirPath );

      if( !pkgDir.exists() || !pkgDir.isDirectory() )
         return null;

      return pkgDir;
   }

   /**
    * 
    */
   private static boolean isJarFile(final File pFile) {
      final boolean aIsJarFile = pFile.toString().endsWith( ".jar" );
      return aIsJarFile;
   }

   
   /**
    * 
    */
   private static List<String> discoverClassNames_fromJarFile(final PkgInfo pkgInfo) throws IOException {

      final List<String> classNames = new LinkedList();
      
      final JarFile jarFile = new JarFile( pkgInfo.path );
      final Enumeration<JarEntry> jarEntries = jarFile.entries();

      while( jarEntries.hasMoreElements() ) {
         JarEntry jarEntry = jarEntries.nextElement();
         
         String simpleName = getClassName_ofPackageAsPath( jarEntry.getName(), pkgInfo.nameAsPath );
         if( simpleName != null ) 
            classNames.add( pkgInfo.name + "." + simpleName );
      }
      
      jarFile.close();
      return classNames;
   }

   
   /**
    * 
    */
   private static void discoverClassNames_fromDirectory( String absPkgPath, String packageName, File packageFolder, List<String> classNames) 
   {
      final File[] aFiles = packageFolder.listFiles();
      for( File aFile : aFiles ) {
         if( aFile.isDirectory() ) {
            discoverClassNames_fromDirectory( absPkgPath, packageName, aFile, classNames );
            continue;
         }

         final String aFileName = aFile.getAbsolutePath().substring( absPkgPath.length() + 1 );
         final boolean aIsClassFile = aFileName.endsWith( ".class" );
         if( !aIsClassFile ) {
            continue;
         }

         final String aSimpleName = aFileName.substring( 0, aFileName.length() - 6 ).replace( '/', '.' ).replace( '\\', '.' );
         final String aClassName = packageName + '.' + aSimpleName;
         classNames.add( aClassName );
      }
   }

   /**
    * 
    */
   private static List<String> discoverClassNames_fromDirectory(PkgInfo pPkgInfo) {

      final List<String> classNames = new LinkedList();
      
      final File aPkgFile = ClassScanner.getPackageFile( pPkgInfo.name, pPkgInfo.path );
      if( aPkgFile == null ) {
         return classNames;
      }

      discoverClassNames_fromDirectory( aPkgFile.getAbsolutePath(), pPkgInfo.name, aPkgFile, classNames );
      return classNames;
   }

   
   
   /**
    * 
    */
   private static class PkgInfo {

      PkgInfo( File pPkgPath, String pPkgName, String pPkgAsPath) {
         this.path = pPkgPath;
         this.name = pPkgName;
         this.nameAsPath = pPkgAsPath;
      }
      
      final File path;
      final String name;
      final String nameAsPath;
   }

   
   /**
    * 
    */
   private static PkgInfo createPackageInfo(Class<?> cls) throws URISyntaxException {

      File pkgPath = new File( cls.getProtectionDomain().getCodeSource().getLocation().toURI() );
      if( pkgPath == null )
         return null;

      String name = cls.getPackage().getName();
      String nameSlash = name.replace( '.', '/' ) + '/';

      return new PkgInfo( pkgPath, name, nameSlash );
   }

   
   
   /**
    * 
    */
   private static List<String> discoverClassNames_inPackage( PkgInfo pkgInfo ) throws IOException {

      assert pkgInfo != null;

      if( pkgInfo.path.isDirectory() )
         return ClassScanner.discoverClassNames_fromDirectory( pkgInfo );
      
      if( !pkgInfo.path.isFile() )
         throw new IllegalStateException("  Package "+pkgInfo.path+" is not in a directory nor .jar file.");
         
      if( !ClassScanner.isJarFile( pkgInfo.path ) )
         throw new IllegalStateException("  Package "+pkgInfo.path+" is in a file which is not a .jar.");
      
      return ClassScanner.discoverClassNames_fromJarFile( pkgInfo );
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
     
      List<Class<? extends Object>> classes = discoverClasses( seedClass, null, Object.class );
      for( Class cls : classes ) {
         System.out.println( "\t" + cls );
      }
   }
   
}

