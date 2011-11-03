package org.jboss.weld.environment.se.jpa.scan;



import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Ondrej Zizka
 */
public class PackageUtils
{
   private static final Logger log = LoggerFactory.getLogger( PackageUtils.class );

   public static List<String> getClassNamesInPackage( String jarName, String packageName ) {
      List<String> classes = new LinkedList();

      packageName = packageName.replaceAll( "\\.", "/" );
      log.debug( "  Scanning jar: " + jarName + "; Looking for: " + packageName );
      
      try {
         JarInputStream jarFile = new JarInputStream( new FileInputStream( jarName ) );
         JarEntry jarEntry;

         while( true )
         {
            jarEntry = jarFile.getNextJarEntry();
            if( jarEntry == null )  break;
            
            String name = jarEntry.getName();
            if( ! name.startsWith( packageName ) ) continue;
            if( !name.endsWith( ".class" ) ) continue;
            String clsName = name.replaceAll( "/", "\\." ).substring(0, name.length() -6);
            log.debug( "  Found " +  clsName );
            classes.add( clsName );
         }
      }
      catch( Exception e ) {
         e.printStackTrace();
      }
      return classes;
   }
}// class

