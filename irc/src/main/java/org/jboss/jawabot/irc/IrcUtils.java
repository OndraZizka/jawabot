
package org.jboss.jawabot.irc;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Ondrej Zizka
 */
public class IrcUtils {
    private static final Logger log = LoggerFactory.getLogger(IrcUtils.class);
    

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
        
        if( null == msg || null == nick )
            return false;

        // Skip blank strings.
        msg = msg.trim();
        nick = nick.trim();
        if( msg.equals("") || nick.equals("") )
            return false;

        // Message starts with nick?
        if( ! msg.toLowerCase().startsWith( nick.toLowerCase() ) )
            return false;

        // At least one char besides the nick.
        if( msg.length() <= nick.length() + 2 )
            return false;

        char charAfterNick = msg.charAt( nick.length() );
        if( charAfterNick == ' ' )
            return true;
        
        // Char after the nick is something that "terminates the nick".
        // Either a space, or comma or colon and space after.
        if( charAfterNick != ',' && charAfterNick != ':' )
            return false;
            
        return ' ' == msg.charAt( nick.length() + 1 );
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
        
        Matcher mat = ( onlyColon ? PAT_MSG_AFTER_NICK_COLON : PAT_MSG_AFTER_NICK ).matcher(msg);
        if( !mat.matches() )
            return Collections.EMPTY_LIST;
        
        String nick = mat.group(1);
        return Collections.singletonList(nick);
   }
   
   /**
    *  TODO: Does not support multiple nicks (yet).
    * 
    *  @returns  A list of nicks given message is for.
    */
   public static List<String> parsePayloadAndRecipients( String msg, boolean onlyColon ) {
        assert (msg != null);
        if( msg.equals( "" ) )
            return Collections.EMPTY_LIST;

        Matcher mat = (onlyColon ? PAT_MSG_AFTER_NICKS_COLON : PAT_MSG_AFTER_NICKS).matcher( msg );
        
        log.debug("  Pattern: " + mat.pattern().toString() );
        
        // No nicks recognized.
        if( !mat.matches() )
            return Collections.singletonList( msg );

        // Some nicksfound.
        List<String> parts = new LinkedList();
        parts.add( mat.group( 3 ) ); // Payload
        parts.add( mat.group( 1 ) ); // First nick
        
        // Other nicks
        final String nicks = mat.group(2);
        if( null == nicks ) return parts;
        
        //parts.addAll( Arrays.asList( nicks.split(", ?") ) );
        parts.addAll( Arrays.asList( StringUtils.split( nicks, ", ") ) );
        
        return parts;
    }
   
   // (:?([a-zA-Z][-_|=~+a-zA-Z0-9]*), ?)+[:,] (.*)
   private static final String NICK = "[a-zA-Z][-_|=~+a-zA-Z0-9]*";
   private static final Pattern PAT_MSG_AFTER_NICK        = Pattern.compile("($N)[:,] .*".replace("$N", NICK));
   private static final Pattern PAT_MSG_AFTER_NICK_COLON  = Pattern.compile("($N): .*"   .replace("$N", NICK));
   private static final Pattern PAT_MSG_AFTER_NICKS       = Pattern.compile("($N)(, ?(?:$N))*[:,] (.*)".replace("$N", NICK));
   private static final Pattern PAT_MSG_AFTER_NICKS_COLON = Pattern.compile("($N)(, ?(?:$N))*: (.*)"   .replace("$N", NICK));

}// class
