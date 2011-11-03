
package org.jboss.jawabot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;

/**
 * Parses the dates:
 *
 * "yyyy-MM-dd" - specified date.
 * +N           - today + N days.
 * today        - today.
 * tmrw         - today + 1 day.
 *
 * TODO: Day names?
 * 
 * @author ondra
 */
public class DateParser {

	private static final String DATE_FORMAT = "yyyy-MM-dd";

	private static final DateFormat df = new SimpleDateFormat(DATE_FORMAT);

	public static String format( Date date ){ return df.format(date); }

	public static Date parse( String str ) throws ParseException {

		Date TODAY = DateUtils.truncate( new Date(), Calendar.DATE );

		// If null, return today.
		if( null == str ){
			return TODAY;
		}

		// First, try "today", "tmrw", "+1" etc.

		if( "today".equals(str))
			return TODAY;
		if( "tmrw".equals(str))
			return DateUtils.addDays(TODAY, 1);
		if( str.startsWith("+") ){
			String rest = str.substring(1);
			if( !StringUtils.isNumeric(rest) )
				throw new ParseException("Offset values are in format '+<days>'. Found: "+str, 1);
			
			int offset = NumberUtils.toInt(rest);
			return DateUtils.addDays(TODAY, offset);
		}


		// Try to parse the date.
		Date date = null;
		try {
			date = df.parse( str );
			return date;
		} catch (ParseException ex) {
			throw ex;
		}

	}// parse()

}
