package org.jboss.jawabot.irc;


import org.jboss.jawabot.JawaBot;
import org.jibble.pircbot.beans.User;
import org.jibble.pircbot.handlers.IrcProtocolEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Just a wrapper which delegates everything to referenced JawaIrcBot.
 *  Used due to the default empty implementations and IRC protocol handlers.
 * 
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
public class JawaBotIrcEventHandler extends IrcProtocolEventHandler {
    private static final Logger log = LoggerFactory.getLogger( JawaBotIrcEventHandler.class );
    
    private JawaIrcBot jawaIrcBot;

    
    public JawaBotIrcEventHandler( JawaIrcBot jawaIrcBot ) {
        super( jawaIrcBot.getConn() );
    }
    

    /**
     */
    @Override
    public void onMessage( String channel, String sender, String login, String hostname, String msgText ) {
        jawaIrcBot.onMessage( channel, sender, login, hostname, msgText );
    }


    /**
     */
    @Override
    public void onPrivateMessage( String sender, String login, String hostname, String msgText ) {
        jawaIrcBot.onPrivateMessage( sender, login, hostname, msgText );
    }
    
    
    /**
     */
    @Override
    public void onChannelInfo( String channel, int userCount, String topic ) {
        jawaIrcBot.onChannelInfo( channel, userCount, topic );
    }
    
    
    @Override
    public void onUserList( String channel, User[] users ) {
        jawaIrcBot.onUserList( channel, users );
    }


    
    // Action
    @Override
    public void onAction( String sender, String login, String hostname, String target, String action ) {
        jawaIrcBot.onAction( sender, login, hostname, target, action );
    }

    
    // Someone joined a channel we're in.
    @Override
    public void onJoin( String channel, String sender, String login, String hostname ) {
        jawaIrcBot.onJoin( channel, sender, login, hostname );
    }


    //  :JawaBot-debug!~PircBot@vpn1-6-95.ams2.redhat.com PART #frien
    @Override
    public void onPart( String channel, String sender, String login, String hostname ) {
        jawaIrcBot.onPart( channel, sender, login, hostname );
    }

    
    @Override
    public void onNickChange( String oldNick, String login, String hostname, String newNick ) {
        jawaIrcBot.onNickChange( oldNick, login, hostname, newNick );
    }

    @Override
    public void onQuit( String sourceNick, String sourceLogin, String sourceHostname, String reason ) {
        jawaIrcBot.onQuit( sourceNick, sourceLogin, sourceHostname, reason );
    }


    @Override
    public void onInvite( String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel ) {
        jawaIrcBot.onInvite( targetNick, sourceNick, sourceLogin, sourceHostname, channel );
    }

    
    
    @Override
    public void onConnect() {
        jawaIrcBot.onConnect();
    }



    @Override
    public void onDisconnect() {
        jawaIrcBot.onDisconnect();
    }

}// class
