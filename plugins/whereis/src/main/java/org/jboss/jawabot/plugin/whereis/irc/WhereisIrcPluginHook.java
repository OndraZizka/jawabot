package org.jboss.jawabot.plugin.whereis.irc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jboss.jawabot.irc.ChannelInfoHandler;
import org.jboss.jawabot.irc.IIrcPluginHook;
import org.jboss.jawabot.irc.IrcBotProxy;
import org.jboss.jawabot.irc.IrcPluginException;
import org.jboss.jawabot.irc.IrcPluginHookBase;
import org.jboss.jawabot.irc.model.IrcMessage;
import org.jibble.pircbot.User;
import org.mortbay.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Scans all channels for users, and on request, tells where given user is or was.
 * 
 *  Every X minutes (configurable interval), visits all channels and updates the info.
 * 
 *  @author Ondrej Zizka
 */
public class WhereisIrcPluginHook extends IrcPluginHookBase implements IIrcPluginHook<Object> {
    private static final Logger log = LoggerFactory.getLogger( WhereisIrcPluginHook.class );
    
    @Inject MemoryWhereIsService whereIsService;
    

    
    // IRC stuff.


    @Override
    public void onMessage( IrcMessage msg, IrcBotProxy bot ) throws IrcPluginException {
        if( ! msg.getText().startsWith("whereis") )
            return;
        
        String pattern = StringUtils.removeStart( msg.getText(), "whereis").trim();
        if( !pattern.contains("*") ){
            List<SeenInfo> occurences = this.whereIsService.whereIsUser( pattern );
            if( occurences.size() == 0 ){
                bot.sendMessage( msg.getUser(), msg.getChannel(), "Sorry, no traces of "+pattern+".");
            }
            else{
                bot.sendMessage( msg.getUser(), msg.getChannel(), this.informAbout( pattern, occurences ) );
            }
        }
    }


    @Override
    public void onPrivateMessage( IrcMessage message, IrcBotProxy bot ) throws IrcPluginException {
        
    }
    
    
   
    
    @Override
    public void onJoin( String channel, String nick, IrcBotProxy bot  ) {
        this.whereIsService.updateUserInfo( nick, channel, new Date() );
    }


    @Override
    public void onBotJoinChannel( String channel, IrcBotProxy bot ) {
        this.scanChannel( channel, bot );
    }


    /**
     *  Scan all channels of the server on connect.
     */
    @Override
    public void onConnect( final IrcBotProxy bot ) {
        ChannelInfoHandler handler = new ChannelInfoHandler() {
            public void onChannelInfo( String channel, int userCount, String topic ) {
                scanChannel( channel, bot );
            }
        };
        bot.listChannels( handler );
    }
    
    
    /**
     *  Gets a list of users on given channel and updates info about their occurrences.
     */
    private void scanChannel( String channel, IrcBotProxy bot ) {
        log.debug("  Scanning channel; " + channel);
        Date now = new Date();
        User[] users = bot.getUsers( channel );
        for( User user : users ) {
            this.whereIsService.updateUserInfo( user, channel, now );
        }
    }


    private static final DateFormat DF_TIME = new SimpleDateFormat("HH:mm:ss");
    private static final DateFormat DF_DATE = new SimpleDateFormat("YYYY-MM-dd");
    
    private String informAbout( String nick, List<SeenInfo> occurences ) {
        Date now = new Date();
        Date hours24Ago = DateUtils.addDays(now, -1);
        
        StringBuilder sb = new StringBuilder(" User " + nick + " was in ");
        for (SeenInfo seenInfo : occurences) {
            sb.append( seenInfo.userOrChannel );
            sb.append( "(" );
            
            DateFormat df = (hours24Ago.before( seenInfo.when ) ? DF_TIME : DF_DATE);
            synchronized( df ){
                sb.append( df.format(seenInfo.when) );
            }
            sb.append( ")" );
        }
        return sb.toString();
    }

    
   
}// class

