
package org.jboss.jawabot.state;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Ondrej Zizka
 */
public class DateStringAdapter extends XmlAdapter<String, Date> {

   private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

   @Override
   public Date unmarshal( String dateString ) throws ParseException {
      return df.parse(dateString);
   }

   @Override
   public String marshal( Date date ){
      return df.format(date);
   }

}// class
