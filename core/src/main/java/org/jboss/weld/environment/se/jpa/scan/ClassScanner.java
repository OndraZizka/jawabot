package org.jboss.weld.environment.se.jpa.scan;



import java.io.*;
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
    * @param pFilterName     - Regular expression to match the desired classes' name (nullif no filtering needed)
    * @param pFilterClass   - The super class of the desired classes (null if no filtering needed)
    * 
    * @return                - The array of matched classes, null if there is a problem.
    * 
    * @author The rest       - Nawaman  http://nawaman.net
    * @author Package as Dir - Jon Peck http://jonpeck.com
    *                              (adapted from http://www.javaworld.com/javaworld/javatips/jw-javatip113.html)
    */
   @SuppressWarnings( "unchecked" )
   public static <T> Class<? extends T>[] DiscoverClasses(
           final Class<?> pSeedClass,
           final String pFilterName,
           final Class<T> pFilterClass) {

      final Pattern aClsNamePattern = (pFilterName == null) ? null : Pattern.compile( pFilterName );

      PkgInfo aPkgInfo = null;

      try {
         aPkgInfo = GetPackageInfoOf( pSeedClass );
      } catch( Throwable e ) {
      }

      if( aPkgInfo == null ) {
         return null;
      }

      final List<String> aClassNames = DiscoverClassNames_inPackage( aPkgInfo );

      if( aClassNames == null ) {
         return null;
      }

      if( aClassNames.size() == 0 ) {
         return null;
      }

      final ArrayList<Class<?>> aClasses = new ArrayList<Class<?>>();
      for( final String aClassName : aClassNames ) {

         if( (aClsNamePattern != null) && !aClsNamePattern.matcher( aClassName ).matches() ) {
            continue;
         }

         // Get the class and filter it
         Class<?> aClass = null;
         try {
            aClass = Class.forName( aClassName );
         } catch( ClassNotFoundException e ) {
            continue;
         } catch( NoClassDefFoundError e ) {
            continue;
         }

         if( (pFilterClass != null) && !pFilterClass.isAssignableFrom( aClass ) ) {
            continue;
         }

         if( pFilterClass != null ) {
            if( !pFilterClass.isAssignableFrom( aClass ) ) {
               continue;
            }

            aClasses.add( aClass.asSubclass( pFilterClass ) );
         } else {
            aClasses.add( aClass );
         }
      }

      Collections.sort( aClasses, ClassComparator.INSTANCE );
      Class<? extends T>[] aClassesArray = aClasses.toArray( (Class<? extends T>[]) (new Class[aClasses.size()]) );

      return aClassesArray;
   }
   

   
   

   static private String GetClassName_afterPackageAsPath( final String pFileName, final String pPkgAsPath)
   {
      final String CName = pFileName.substring( 0, pFileName.length() - 6 ).replace( '/', '.' ).replace( '\\', '.' );
      final String CName_AfterPackageAsPath = CName.substring( pPkgAsPath.length() );
      return CName_AfterPackageAsPath;
   }

   static private String GetClassName_ofPackageAsPath( final String pFileName, final String pPkgAsPath) { 
      final boolean aIsClass = pFileName.endsWith( ".class" );
      if( !aIsClass ) {
         return null;
      }

      final boolean aIsBelongToPackage = pFileName.startsWith( pPkgAsPath );
      if( !aIsBelongToPackage ) {
         return null;
      }

      final String aClassName = ClassScanner.GetClassName_afterPackageAsPath( pFileName, pPkgAsPath );
      return aClassName;
   }

   static private File GetPackageFile(
           final String pPkgName,
           final File pPkgPath) {

      final String aPkgFileName = pPkgPath.getAbsoluteFile().toString() + '/' + pPkgName.replace( '.', '/' );
      final File aPkgFile = new File( aPkgFileName );

      final boolean aIsExist = aPkgFile.exists();
      final boolean aIsDirectory = aPkgFile.isDirectory();
      final boolean aIsExist_asDirectory = aIsExist && aIsDirectory;
      if( !aIsExist_asDirectory ) {
         return null;
      }

      return aPkgFile;
   }

   static private boolean Check_isJarFile(final File pFile) {
      final boolean aIsJarFile = pFile.toString().endsWith( ".jar" );
      return aIsJarFile;
   }

   static private List<String> DiscoverClassNames_fromJarFile(final PkgInfo pPkgInfo) {

      final ArrayList<String> aClassNames = new ArrayList<String>();
      try {
         final JarFile JF = new JarFile( pPkgInfo.PkgPath );
         final Enumeration<JarEntry> JEs = JF.entries();

         while(JEs.hasMoreElements()) {
            final JarEntry aJE = JEs.nextElement();
            final String aJEName = aJE.getName();

            final String aSimpleName = GetClassName_ofPackageAsPath( aJEName, pPkgInfo.PkgAsPath );
            if( aSimpleName == null ) {
               continue;
            }

            final String aClassName = pPkgInfo.PkgName + '.' + aSimpleName;
            aClassNames.add( aClassName );
         }

         JF.close();
      } catch( IOException e ) {
      }

      return aClassNames;
   }

   static private void DiscoverClassNames_fromDirectory(
           final String pAbsolutePackagePath,
           final String pPackageName,
           final File pPackageFolder,
           final ArrayList<String> pClassNames) 
   {
      final File[] aFiles = pPackageFolder.listFiles();
      for( File aFile : aFiles ) {
         if( aFile.isDirectory() ) {
            DiscoverClassNames_fromDirectory( pAbsolutePackagePath, pPackageName, aFile, pClassNames );
            continue;
         }

         final String aFileName = aFile.getAbsolutePath().substring( pAbsolutePackagePath.length() + 1 );
         final boolean aIsClassFile = aFileName.endsWith( ".class" );
         if( !aIsClassFile ) {
            continue;
         }

         final String aSimpleName = aFileName.substring( 0, aFileName.length() - 6 ).replace( '/', '.' ).replace( '\\', '.' );
         final String aClassName = pPackageName + '.' + aSimpleName;
         pClassNames.add( aClassName );
      }
   }

   static private List<String> DiscoverClassNames_fromDirectory(PkgInfo pPkgInfo) {

      final ArrayList<String> aClassNames = new ArrayList<String>();
      final File aPkgFile = ClassScanner.GetPackageFile( pPkgInfo.PkgName, pPkgInfo.PkgPath );
      if( aPkgFile == null ) {
         return aClassNames;
      }

      DiscoverClassNames_fromDirectory( aPkgFile.getAbsolutePath(), pPkgInfo.PkgName, aPkgFile, aClassNames );
      return aClassNames;
   }

   
   
   
   static public class PkgInfo {

      PkgInfo(
              final File pPkgPath,
              final String pPkgName,
              final String pPkgAsPath) {

         this.PkgPath = pPkgPath;
         this.PkgName = pPkgName;
         this.PkgAsPath = pPkgAsPath;
      }
      final File PkgPath;
      final String PkgName;
      final String PkgAsPath;
   }

   
   
   static public PkgInfo GetPackageInfoOf(Class<?> pClass) {
      File aPkgPath = null;
      String aPkgName = null;
      String aPkgAsPath = null;

      try {
         aPkgPath = new File( pClass.getProtectionDomain().getCodeSource().getLocation().toURI() );
         aPkgName = pClass.getPackage().getName();
         aPkgAsPath = aPkgName.replace( '.', '/' ) + '/';
      } catch( Throwable e ) {
      }

      if( aPkgPath == null ) {
         return null;
      }

      final PkgInfo aPkgInfo = new PkgInfo( aPkgPath, aPkgName, aPkgAsPath );
      return aPkgInfo;
   }

   
   
   static public List<String> DiscoverClassNames_inPackage(final PkgInfo pPkgInfo) {

      if( pPkgInfo == null ) {
         return null;
      }

      List<String> aClassNames = new ArrayList();
      if( pPkgInfo.PkgPath.isDirectory() ) {

         aClassNames = ClassScanner.DiscoverClassNames_fromDirectory( pPkgInfo );

      } else if( pPkgInfo.PkgPath.isFile() ) {
         boolean aIsJarFile = ClassScanner.Check_isJarFile( pPkgInfo.PkgPath );
         if( !aIsJarFile ) {
            return null;
         }

         aClassNames = ClassScanner.DiscoverClassNames_fromJarFile( pPkgInfo );
      }

      return aClassNames;
   }

   
   
   
   
   

   public static void main(String... pArgs) {
      Class<?> aSeedClass = ClassScanner.class;
      try {
         aSeedClass = Class.forName( pArgs[0] );
      } catch( Exception E ) {
      }

      if( aSeedClass == null ) {
         aSeedClass = ClassScanner.class;
      }

      final Class<?>[] aClasses = DiscoverClasses( aSeedClass, null, null );

      System.out.println( "[" );
      if( aClasses != null ) {
         for( Class aClass : aClasses ) {
            System.out.println( "\t" + aClass );
         }
      }
      System.out.println( "]" );
   }
}



// ----------------------------------------------------------------------

class StringComparator implements Comparator<String> {

   public static final StringComparator INSTANCE = new StringComparator();

   /**
    * Compares two strings.
    **/
   public int compare(final String s1, final String s2) {
      if( s1 == s2 ) return 0;
      if( s1 == null ) return -1;
      if( s2 == null ) return 1;
      if( s1.equals( s2 ) ) return 0;
      return s1.compareTo( s2 );
   }
}


class ClassComparator implements Comparator<Class<?>> {

   public static final ClassComparator INSTANCE = new ClassComparator();

   /** Compares two classes. */
   public int compare(final Class<?> c1, final Class<?> c2) {

      if( c1 == c2 ) return 0;
      if( c1 == null ) return -1;
      if( c2 == null ) return 1;
      if( c1.equals( c2 ) ) return 0;

      return c1.getCanonicalName().compareTo( c2.getCanonicalName() );
   }
}
