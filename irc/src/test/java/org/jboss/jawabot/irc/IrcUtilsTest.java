package org.jboss.jawabot.irc;

import java.util.List;
import junit.framework.TestCase;


/**
 *
 * @author ondra
 */
public class IrcUtilsTest extends TestCase {
    
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


    public void testIsMsgForNick() {
        System.out.println( "isMsgForNick" );
        String[][] msgs = new String[][]{
            {"ozizka", "ozizka-dinner, are you back?"},
            {"ozizka", "ozizka_wfh: work work work"},
            {"ozizka", "ozizka|mtg"},
            {"ozizka", "ozizka1"}
        };
        for( String[] msg : msgs ) {
            assertTrue( IrcUtils.isMsgForNick( msg[1], msg[0] ) );
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

}
