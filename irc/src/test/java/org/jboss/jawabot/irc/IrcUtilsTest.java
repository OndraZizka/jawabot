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
            {"ozizka-dinner","ozizka"},
            {"ozizka_wfh",   "ozizka"},
            {"ozizka|mtg",   "ozizka"},
            {"ozizka1",      "ozizka"}
        };
        for( String[] nick : nicks ) {
            assertEquals( nick[1], IrcUtils.normalizeUserNick( nick[0] ) );
        }
    }


    public void XtestIsMsgForNick() {
        System.out.println( "isMsgForNick" );
        String msg = "";
        String nick = "";
        boolean expResult = false;
        boolean result = IrcUtils.isMsgForNick( msg, nick );
        assertEquals( expResult, result );
        fail( "The test case is a prototype." );
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
