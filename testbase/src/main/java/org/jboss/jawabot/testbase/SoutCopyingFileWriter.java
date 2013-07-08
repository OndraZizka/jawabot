
package org.jboss.jawabot.testbase;

import java.io.*;
import org.apache.commons.io.FileUtils;


/**
 *  A FileWriter which copies everything written to it to standard output.
 * 
 *  @author Ondrej Zizka
 */
public class SoutCopyingFileWriter extends FileWriter {

   public SoutCopyingFileWriter(File file) throws IOException {
       super( createParentDirs(file) );
   }
   public SoutCopyingFileWriter(String fileName) throws IOException {
       super( createParentDirs(fileName) );
   }
   
   private static File createParentDirs( File file ) throws IOException {
       FileUtils.forceMkdir( file.getParentFile() );
       return file;
   }

   private static String createParentDirs( String fileName ) throws IOException {
       File file = new File( fileName );
       FileUtils.forceMkdir( file.getParentFile() );
       return fileName;
   }

   @Override
   public void write(int c) throws IOException {
      System.out.write(c);
      super.write(c);
   }

   @Override
   public void write(char[] cbuf, int off, int len) throws IOException {
      System.out.write( new String(cbuf).getBytes(), off, len);
      super.write(cbuf, off, len);
   }

   @Override
   public void write(String str, int off, int len) throws IOException {
      System.out.write( str.getBytes(), off, len);
      super.write(str, off, len);
   }

}
// class
