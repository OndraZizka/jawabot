
package org.jboss.jawabot;

import org.jboss.jawabot.ex.JawaBotIOException;
import org.jboss.jawabot.ex.JawaBotException;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import java.io.FileReader;
import java.io.Writer;
import java.util.Date;
import org.apache.commons.lang.time.DateUtils;








/**
 *
 * @author Ondrej Zizka
 */
@SuppressWarnings("static-access")
public class JaxbStatusTest1 /*extends TestCase*/ {

   public void XtestWrite() throws JawaBotIOException, JawaBotException, IOException {

      // TODO: Replace JawaBot with ResourceManager only...
      /*JawaBot bot = new JawaBot();

      // Make some reservations...
      Date today = new Date();
      Date today5 = DateUtils.addDays( today, 5 );
      Date today6 = DateUtils.addDays( today, 6 );
      Date today10 = DateUtils.addDays( today, 10 );
      
      bot.getResourceManager().bookResource("jawa01", "ozizka", today, today5 );
      bot.getResourceManager().bookResource("jawa01", "oskutka", today6, today10 );
      bot.getResourceManager().bookResource("jawa02", "ozizka", today, today10 );

      // Write the XML.
      JaxbStateWriter wr = new JaxbStateWriter();
      Writer writer = new SoutCopyingFileWriter("test.xml");
      wr.write(bot, writer );
       */
      
   }


   public void XtestRead() throws JawaBotIOException, JawaBotException, IOException, JAXBException
   {
      /*
      JaxbStateReader re = new JaxbStateReader();
      FileReader reader = new FileReader("test.xml");
      JawaBot bot = re.read(reader);

      JaxbStateWriter wr = new JaxbStateWriter();
      Writer writer = new SoutCopyingFileWriter("test.xml");
      wr.write(bot, writer );
       */
   }



}// class
