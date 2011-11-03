package org.jboss.jawabot.plugin.whereis.irc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jboss.jawabot.irc.ChannelInfoHandler;
import org.jboss.jawabot.irc.IIrcPluginHook;
import org.jboss.jawabot.irc.IrcBotProxy;
import org.jboss.jawabot.irc.IrcPluginException;
import org.jboss.jawabot.irc.IrcPluginHookBase;
import org.jboss.jawabot.irc.UserListHandler;
import org.jboss.jawabot.irc.model.IrcMessage;
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
    private static final Logger logScan = LoggerFactory.getLogger( WhereisIrcPluginHook.class.getName()+".channelScanQueueProcessor" );
    
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
    public void onConnect( final IrcBotProxy bot )
    {
        log.info(" onConnect();  Will now scan all channels.");
        
        // Create a queue for channels, sorted by user count.
        final Queue<ChannelInfo> scanQueue = new PriorityQueue<ChannelInfo>(800, ChannelInfo.USER_COUNT_COMPARATOR );
        
        ChannelInfoHandler handler = new ChannelInfoHandler() {
            public void onChannelInfo( String channel, int userCount, String topic ) {
                //scanChannel( channel, bot );
                scanQueue.add( new ChannelInfo(channel, userCount, topic) );
            }
        };
        bot.listChannels( handler );
        
        // Schedule channel scanning in other thread.
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable scanJob =
            new Runnable() {
                public void run() {
                    ChannelInfo chi = scanQueue.poll();
                    if( null == chi ){
                        logScan.debug("  No more channels in scan queue. ");
                        executor.shutdown();
                    }
                    else {
                        logScan.debug("  Scanning " + chi.toString() );
                        scanChannel(null, bot);
                    }
                }
            };

        // Wait until the channels are downloaded and start scanning them.
        final int expectedChannelDownloadDurationMs = 3000;
        final int delayBetweenChannels = 1000;
        executor.scheduleWithFixedDelay( scanJob, expectedChannelDownloadDurationMs, delayBetweenChannels, TimeUnit.MILLISECONDS);
    }
    
    
    /**
     *  Gets a list of users on given channel and updates info about their occurrences.
     */
    private void scanChannel( String channel, IrcBotProxy bot ) {
        log.debug("  Scanning channel; " + channel);
        final Date now = new Date();
        UserListHandler handler = 
        new UserListHandler() {
            public void onUserList( String channel, User[] users ) {
                for( User user : users ) {
                    whereIsService.updateUserInfo( user, channel, now );
                }
            }
        };
        bot.listUsersInChannel(channel, handler);
    }


    private static final DateFormat DF_TIME = new SimpleDateFormat("HH:mm:ss");
    private static final DateFormat DF_DATE = new SimpleDateFormat("yyyy-MM-dd");
    
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



/**
 *  Data class.
 */
class ChannelInfo {
    String name;
    int userCount;
    String topic;

    public ChannelInfo( String name, int userCount, String topic ) {
        this.name = name;
        this.userCount = userCount;
        this.topic = topic;
    }


    @Override
    public String toString() {
        return "#" + name + "("+userCount+")";
    }

    
    public static final 
        Comparator<ChannelInfo> USER_COUNT_COMPARATOR = new Comparator<ChannelInfo>() {
            public int compare( ChannelInfo o1, ChannelInfo o2 ) {
                return o2.userCount - o1.userCount; // Bigger channels first.
            }
        };
    
}