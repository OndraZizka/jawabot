
package org.jboss.jawabot.irc;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Ondrej Zizka
 */
public class IrcUtils {

   // Use only first part consisting of lowercase latin alphabetic chars as user name.
   // This normalizes user names like ozizka-dinner, ozizka_wfh or ozizka1.
   public static String normalizeUserNick( String nick ){

      int nonAlnum = StringUtils.indexOfAnyBut(nick, "abcdefghijklmnopqrstuvwxyz");
      if( nonAlnum < 1 ) return nick;
		return nick.substring(0, nonAlnum);

   }


}// class
