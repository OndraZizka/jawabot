
package org.jboss.jawabot;

import java.io.*;


/**
 *
 * @author Ondrej Zizka
 */
public class SoutCopyingFileWriter extends FileWriter {

   public SoutCopyingFileWriter(File file) throws IOException {      super(file);   }
   public SoutCopyingFileWriter(String fileName) throws IOException {      super(fileName);   }

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
