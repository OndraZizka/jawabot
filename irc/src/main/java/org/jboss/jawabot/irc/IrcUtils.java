
package org.jboss.jawabot.irc;

import com.google.common.collect.Collections2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Ondrej Zizka
 */
public class IrcUtils {

   /**
    * Use only first part consisting of lowercase latin alphabetic chars as user name.
    * This normalizes user names like ozizka-dinner, ozizka_wfh or ozizka1.
    */
   public static String normalizeUserNick( String nick ){

      int nonAlnum = StringUtils.indexOfAnyBut(nick, "abcdefghijklmnopqrstuvwxyz");
      if( nonAlnum < 1 ) return nick;
		return nick.substring(0, nonAlnum);

   }

   
   /**
    * Does not support multiple nicks.
    * Returns false if the message consists only of the nick.
    *
    * @param msg   IRC message, like "ozizka: How are you?"
    * @param nick  User nick, like "ozizka".
    * @returns true if the message is intended for the given nick.
    *
    * TODO: Return the position of the end of the prolog (start of the actual message).
    */
   public static boolean isMsgForNick( String msg, String nick ) {
      if( null == msg || msg.equals("") || null == nick || nick.equals("") )
         return false;

      return msg.toLowerCase().startsWith( nick.toLowerCase() )
          // At least one char besides the nick.
          && msg.length() > nick.length() + 2
          // Char after the nick is something that "terminates the nick".
          && StringUtils.contains(" ,:", msg.charAt( nick.length() ) );
   }
   
   
   /**
    * @returns  0 if the message is not for given nick,
    *           or position of start of the message after nick.
    * TODO:  Support multiple nicks:  "ozizka, pskopek: Msg."
    */
   public static int getMsgStartAfterNick( String msg, String nick ) {
      if( null == msg || msg.equals("") || null == nick || nick.equals("") )
         return 0;

      if( ! msg.toLowerCase().startsWith( nick.toLowerCase() ) )
          return 0;
      
      int nickLen = nick.length();
      
      // At least one char besides the nick.
      if( msg.length() <= nickLen + 2 )
          return 0;
      
      // Char after the nick is something that "terminates the nick".
      if( ! StringUtils.contains(" ,:", msg.charAt( nickLen ) ) )
          return 0;
      
      String afterNick = msg.substring( nickLen );
      return nickLen + StringUtils.indexOfAnyBut( afterNick, " ,:");          
   }


   
   /**
    *  @returns  whoIsThisMsgFor(msg, false);
    */
   public static List<String> whoIsThisMsgFor( String msg ) {
       return whoIsThisMsgFor(msg, false);
   }
   
   /**
    *  TODO: Does not support multiple nicks (yet).
    * 
    *  @returns  A list of nicks given message is for.
    */
   public static List<String> whoIsThisMsgFor( String msg, boolean onlyColon ) {
        assert( msg != null );
        if( msg.equals("") )
            return Collections.EMPTY_LIST;
        
        Matcher mat = ( onlyColon ? MESSAGE_AFTER_NICK_COLON_PATTERN : MESSAGE_AFTER_NICK_PATTERN ).matcher(msg);
        if( !mat.matches() )
            return Collections.EMPTY_LIST;
        
        String nick = mat.group(1);
        return Collections.singletonList(nick);
   }
   
   private static final Pattern MESSAGE_AFTER_NICK_PATTERN = Pattern.compile("([a-zA-Z][-_|=~+a-zA-Z0-9]*)[:,] .*");
   private static final Pattern MESSAGE_AFTER_NICK_COLON_PATTERN = Pattern.compile("([a-zA-Z][-_|=~+a-zA-Z0-9]*): .*");


}// class
