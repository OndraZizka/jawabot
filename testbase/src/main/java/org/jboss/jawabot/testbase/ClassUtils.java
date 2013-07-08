package org.jboss.jawabot.testbase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Works with classpaths, classes, various searching etc.
 * 
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
public class ClassUtils {
    private static final Logger log = LoggerFactory.getLogger( ClassUtils.class );

    
    public static File findClassOriginFile( Class cls ){
        // Try to find the class file.
        try {
            final URL url = cls.getClassLoader().getResource( cls.getName().replace('.', '/') + ".class");
            final File file = new File( url.getFile() ); // toString()
            if( file.exists() )
                return file;
        }
        catch( Exception ex ) { }
        
        // Method 2
        try {
            URL url = cls.getProtectionDomain().getCodeSource().getLocation();
            final File file = new File( url.getFile() ); // toString()
            if( file.exists() )
                return file;
        }
        catch( Exception ex ) { }
        
        return null;
    }
    
    public static String findClassPathRootFor( Class cls ) {
        File clsFile = findClassOriginFile( cls );
        log.debug("Class' file: " + clsFile);
        if( clsFile == null )  return null;
        //clsFile.getName().endsWith(".class") )
        
        String clsSubPath = cls.getName().replace('.','/') + ".class";
        return StringUtils.removeEnd( clsFile.getPath(), clsSubPath );
    }


    // ======= Class utils ====== //
    public static File copyResourceToDir( Class cls, String name, File dir ) throws IOException {
        String packageDir = cls.getPackage().getName().replace( '.', '/' );
        String path = "/" + packageDir + "/" + name;
        InputStream is = cls.getResourceAsStream( path );
        if( is == null ) {
            throw new IllegalArgumentException( "Resource not found: " + packageDir );
        }
        File file = new File( dir, name );
        FileUtils.copyInputStreamToFile( is, file );
        return file;
    }


    static boolean containsClass( JarFile jarFile, String classFilePath ) {
        final Enumeration<JarEntry> entries = jarFile.entries();
        while( entries.hasMoreElements() ) {
            final JarEntry entry = entries.nextElement();
            if( (!entry.isDirectory()) && entry.getName().contains( classFilePath ) ) {
                return true;
            }
        }
        return false;
    }


}// class
