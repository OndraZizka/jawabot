package org.jboss.jawabot.plugin.whereis.irc;

import java.util.Date;
import javax.inject.Inject;
import org.jboss.jawabot.irc.ChannelInfoHandler;
import org.jboss.jawabot.irc.IIrcPluginHook;
import org.jboss.jawabot.irc.IrcBotProxy;
import org.jboss.jawabot.irc.IrcPluginHookBase;
import org.jibble.pircbot.User;
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
    public void onJoin( String channel, String user, IrcBotProxy bot  ) {
    }


    @Override
    public void onBotJoinChannel( String channel, IrcBotProxy bot ) {
        this.scanChannel( channel, bot );
    }


    @Override
    public void onConnect( final IrcBotProxy bot ) {
        ChannelInfoHandler handler = new ChannelInfoHandler() {
            public void onChannelInfo( String channel, int userCount, String topic ) {
                scanChannel( channel, bot );
            }
        };
        bot.listChannels( handler );
    }
    
    
    private void scanChannel( String channel, IrcBotProxy bot ) {
        Date now = new Date();
        User[] users = bot.getUsers( channel );
        for( User user : users ) {
            this.whereIsService.updateUserInfo( user, channel, now );
        }
    }

    
   
}// class

