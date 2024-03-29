
package cz.dynawest.util;

import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author Ondrej Zizka
 */
public class DateUtils extends org.apache.commons.lang.time.DateUtils {

   public static Date getEarlier( Date a, Date b ) {
      return a.before(b) ? a : b;
   }

   public static Date getLater( Date a, Date b ) {
      return a.before(b) ? b : a;
   }

   public static int getDaysDiff( Date date1, Date date2 ) {

      Calendar cal = Calendar.getInstance();
      cal.setTime(date1);
      int d1 = cal.get(Calendar.DATE);

      cal.setTime(date2);
      int d2 = cal.get(Calendar.DATE);

      return d2 - d1;
   }

   public static String toStringSQL( Date date ){
      if( null == date ) return "";
      StringBuilder sb = new StringBuilder(10);
      sb.append( date.getYear()+1900 ).append('-');
      if(date.getMonth() < 9 ) sb.append('0');
      sb.append(date.getMonth()+1).append('-');
      if(date.getDate() < 10 ) sb.append('0');
      sb.append(date.getDate());
      return sb.toString();
   }

   public static Date fromStringSQL( String str ){
      String[] parts = str.split("-");
      if( parts.length != 3 ) return null;

      int y = NumberUtils.toInt( parts[0], -1);
      if( y == -1 ) return null;
      if( y < 1900 ) return null;

      int m = NumberUtils.toInt( parts[1], -1);
      if( m == -1 ) return null;

      int d = NumberUtils.toInt( parts[2], -1);
      if( d == -1 ) return null;

      return new Date(y-1900, m, d);
   }

   
   /**
    *  Not much precise but who cares.
    *  @returns  String with relative time representation like "2 days ago" or "yesterday".
    */
   public static String createRelativeTimeString( Date when ) {
      
      if( null == when )
         return "";
      
      Date now = new Date();
      
      long diffSec = (now.getTime() - when.getTime()) / 1000;
      
      if( diffSec < 10 ) return "now";
      if( diffSec < 60 ) return "seconds ago";
      if( diffSec < 120 ) return "a minute ago";
      if( diffSec < 55 * 60 ) return diffSec/60 + " min ago";
      if( diffSec < 2* 60 * 60 ) return "an hour ago";
      if( diffSec < 23 * 60 * 60 ) return diffSec/3600 + " hours ago";
      if( diffSec < 36 * 60 * 60 ) return " yesterday";
      
      long diffDays = diffSec / (24  * 60 * 60);
      if( diffDays < 30 ) return diffDays + " days ago";
      if( diffDays < 365  ) return diffDays/30 + " months ago";
      return diffDays/365 + " years ago";
      
   }

}// class
