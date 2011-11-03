
package org.jboss.jawabot.test;

import org.jboss.jawabot.testbase.SoutCopyingFileWriter;
import org.jboss.jawabot.ex.JawaBotIOException;
import org.jboss.jawabot.ex.JawaBotException;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import org.jboss.jawabot.config.*;
import junit.framework.TestCase;
import org.jboss.jawabot.config.beans.ConfigBean;








/**
 *
 * @author Ondrej Zizka
 */
@SuppressWarnings("static-access")
public class JaxbConfigTest extends TestCase {

   public void XtestConfigWrite() throws JawaBotIOException, JawaBotException, IOException {

      ConfigBean cb = new ConfigBean();

      // Write the XML.
      ConfigPersister cp = new JaxbConfigPersister( new SoutCopyingFileWriter("testConfig.xml") );
      cp.save( cb );
      
   }


   public void testConfigRead() throws JawaBotIOException, JawaBotException, IOException, JAXBException
   {
      JaxbConfigPersister jcp = new JaxbConfigPersister();
      ConfigBean cb = jcp.load();
   }



}// class


