package org.jboss.jawabot.irc;

import java.util.List;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author ondra
 */
public class IrcUtilsTest extends TestCase {
    private static final Logger log = LoggerFactory.getLogger(IrcUtilsTest.class);

    
    public IrcUtilsTest( String testName ) {
        super( testName );
    }

    public void testNormalizeUserNick() {
        System.out.println( "normalizeUserNick" );
        String[][] nicks = new String[][]{
            {"ozizka","ozizka"},
            {"ozizka-dinner","ozizka"},
            {"ozizka_wfh",   "ozizka"},
            {"ozizka|mtg",   "ozizka"},
            {"ozizka1",      "ozizka"}
        };
        for( String[] nick : nicks ) {
            assertEquals( nick[1], IrcUtils.normalizeUserNick( nick[0] ) );
        }
    }


    /**
     *   isMgsForNick() actually doesn't support suffixes... yet.
     */
    public void testIsMsgForNick() {
        System.out.println( "isMsgForNick" );
        String[][] msgs = new String[][]{
            {"ozizka", "ozizka, are you back?"},
            {"ozizka", "ozizka: work work work"},
            {"ozizka", "ozizka ping"}
            /*
            {"ozizka", "ozizka-dinner, are you back?"},
            {"ozizka", "ozizka_wfh: work work work"},
            {"ozizka", "ozizka|mtg ping"},
            {"ozizka", "ozizka1 ping"}
            */
        };
        for( String[] msg : msgs ) {
            assertTrue( "Message: "+msg[1], IrcUtils.isMsgForNick( msg[1], msg[0] ) );
        }
        String[][] msgs2 = new String[][]{
            {"ozizk",  "ozizka-dinner, are you back?"},
            {"ozizka", "ozizk_wfh: work work work"}
        };
        for( String[] msg : msgs2 ) {
            assertFalse( IrcUtils.isMsgForNick( msg[1], msg[0] ) );
        }
    }


    public void XtestGetMsgStartAfterNick() {
        System.out.println( "getMsgStartAfterNick" );
        String msg = "";
        String nick = "";
        int expResult = 0;
        int result = IrcUtils.getMsgStartAfterNick( msg, nick );
        assertEquals( expResult, result );
        fail( "The test case is a prototype." );
    }


    public void XtestWhoIsThisMsgFor_String() {
        System.out.println( "whoIsThisMsgFor" );
        String msg = "";
        List expResult = null;
        List result = IrcUtils.whoIsThisMsgFor( msg );
        assertEquals( expResult, result );
        fail( "The test case is a prototype." );
    }


    public void XtestWhoIsThisMsgFor_String_boolean() {
        System.out.println( "whoIsThisMsgFor" );
        String msg = "";
        boolean onlyColon = false;
        List expResult = null;
        List result = IrcUtils.whoIsThisMsgFor( msg, onlyColon );
        assertEquals( expResult, result );
        fail( "The test case is a prototype." );
    }

    public void testParsePayloadAndRecipients_simple() {
        // (:?([a-zA-Z][-_|=~+a-zA-Z0-9]*), ?)+[:,] (.*)
        String msg = "gpark: how are you today?";
        List parts = IrcUtils.parsePayloadAndRecipients( msg, false );
        assertEquals("2 parts returned", 2, parts.size() );
        assertEquals("1st part is the message", "how are you today?", parts.get(0) );
        assertEquals("2nd part is the nick", "gpark", parts.get(1) );
    }

    public void testParsePayloadAndRecipients_noNick() {
        String msg = "how are you today?";
        List parts = IrcUtils.parsePayloadAndRecipients( msg, false );

        log.info( msg + " ==> " + StringUtils.join( parts, " | ") );
        assertEquals("1 part returned", 1, parts.size() );
        assertEquals("1st part is the message", "how are you today?", parts.get(0) );
    }

    public void testParsePayloadAndRecipients_comma() {
        String msg = "gpark, how are you today?";
        List parts = IrcUtils.parsePayloadAndRecipients( msg, false );

        log.info( msg + " ==> " + StringUtils.join( parts, " | ") );
        assertEquals("2 parts returned", 2, parts.size() );
        assertEquals("1st part is the message", "how are you today?", parts.get(0) );
        assertEquals("2nd part is the nick", "gpark", parts.get(1) );
    }

    public void testParsePayloadAndRecipients_multipleNicks() {
        String msg = "gpark, ozizka: how are you today?";
        List parts = IrcUtils.parsePayloadAndRecipients( msg, false );
        
        log.info( msg + " ==> " + StringUtils.join( parts, " | ") );
        assertEquals("3 parts returned", 3, parts.size() );
        assertEquals("1st part is the message", "how are you today?", parts.get(0) );
        assertEquals("2nd part is the nick", "gpark", parts.get(1) );
        assertEquals("3rd part is the nick", "ozizka", parts.get(2) );
    }

}// class
