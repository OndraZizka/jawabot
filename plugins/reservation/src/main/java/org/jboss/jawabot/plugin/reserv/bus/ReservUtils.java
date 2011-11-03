
package org.jboss.jawabot.plugin.reserv.bus;

import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.jboss.jawabot.plugin.reserv.bus.ResourceManager.ReservationsBookingResult;

/**
 *
 * @author Ondrej Zizka
 */
public class ReservUtils {

   /**
    *  Formats a string depending on whether it's two same dates or not:
    *  "from {date1} to {date2}"
    *  "for {date1}"
    *  or "around ... " if isMeregdResult is true.
    */
   public static String formatTwoDatesString( Date fromDate, Date toDate, boolean isMergedResult ) {

      String from = DateParser.format( fromDate );
		String to   = DateParser.format( toDate );

      final String SUBJ_PREPOSITION = isMergedResult ? "around" : "from";
      final String SUBJ_PERIOD_FORMAT = StringUtils.equals(from, to) ? "for %s" : SUBJ_PREPOSITION+" %s to %s";
      return String.format( SUBJ_PERIOD_FORMAT, from, to );
   }


   /**
    * For a single reservation.
    * Taking jawa18 for 2009-02-17   or
    * Taking jawa18 from 2009-11-17 to 2009-12-24
    */
   public static String formatReservationInfoLine( String resName, ReservationWrap reservation ) {

      boolean isMergedReservation = reservation.type == ReservationWrap.Type.MERGED;

      String twoDatesStr = ReservUtils.formatTwoDatesString(
                 reservation.getFrom(),
                 reservation.getTo(),
                 false );

      //final String SUBJ_FORMAT = StringUtils.equals(from, to) ? "%s %s for %s" : "%s %s from %s to %s";
      final String SUBJ_FORMAT = "%s %s " + twoDatesStr;

      String subject = String.format( SUBJ_FORMAT,
                              (isMergedReservation) ? "Taking" : "Keeping",
                              resName );
      return subject;
   }

   
   /** For multiple reservations. */
   public static String formatReservationInfoLine( ReservationsBookingResult bookingResult ) {

      boolean isMergedResult = bookingResult.type == ReservationWrap.Type.MERGED;

      String twoDatesStr = ReservUtils.formatTwoDatesString(
                 bookingResult.claimedResv.getFrom(),
                 bookingResult.claimedResv.getTo(),
                 isMergedResult );

      final String SUBJ_FORMAT = "%s %s " + twoDatesStr;

      String subject = String.format( SUBJ_FORMAT,
                              (isMergedResult) ? "Taking / keeping" : "Taking",
										StringUtils.join( bookingResult.resourceNames, ", ") );
      return subject;
   }


}// class
