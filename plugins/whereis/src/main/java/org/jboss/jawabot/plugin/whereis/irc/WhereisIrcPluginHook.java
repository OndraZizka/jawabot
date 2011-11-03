package org.jboss.jawabot.plugin.whereis.irc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
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
import org.jboss.jawabot.irc.UserListHandlerBase;
import org.jboss.jawabot.irc.ent.IrcEvJoin;
import org.jboss.jawabot.irc.ent.IrcEvMessage;
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
		
		
		private static final int MIN_USER_COUNT_TO_SCAN_CHANNEL = 10;
    

    // TODO: Prevent multiple scanning over this.
    Set<String> channelsBeingScanned = new ConcurrentSkipListSet();
    
    
    
    // IRC stuff.


    @Override
    public void onMessage( IrcEvMessage msg, IrcBotProxy bot ) throws IrcPluginException {
        if( ! msg.getText().startsWith("whereis") )
            return;
        
        String pattern = StringUtils.removeStart( msg.getText(), "whereis").trim();
        
        // No wildcards -> search exact nick.
        if( !pattern.contains("*") ){
            List<SeenInfo> occurrences = this.whereIsService.whereIsUser( pattern );
            if( occurrences.size() == 0 ){
                bot.sendMessage( msg.getUser(), msg.getChannel(), "Sorry, no traces of "+pattern+".");
            }
            else{
                bot.sendMessage( msg.getUser(), msg.getChannel(), this.informAbout( pattern, occurrences ) );
            }
        }
        // Wildcards, list all matching nicks.
        else{
            Map<String, Set<SeenInfo>> users = this.whereIsService.searchUser( pattern );
            if( users.size() == 0 ){
                bot.sendMessage( msg.getUser(), msg.getChannel(), "Sorry, no traces of "+pattern+".");
            }
            else{
                for( Map.Entry<String, Set<SeenInfo>> entry : users.entrySet() ) {
                    bot.sendMessage( msg.getUser(), msg.getChannel(), this.informAbout( entry.getKey(), new ArrayList(entry.getValue()) ) );
                }
            }
        }
    }


    @Override
    public void onPrivateMessage( IrcEvMessage message, IrcBotProxy bot ) throws IrcPluginException {
        this.onMessage( message, bot );
    }
    
    
   
    
    @Override
    public void onJoin( IrcEvJoin event, IrcBotProxy bot  ) {
        this.whereIsService.updateUserInfo( event.getChannel(), event.getUser(), new Date() );
    }


    @Override
    public void onBotJoinChannel( String channel, IrcBotProxy bot ) {
        log.debug("  onBotJoinChannel(): " + channel);
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
								if( userCount >= MIN_USER_COUNT_TO_SCAN_CHANNEL )
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
                        scanChannel(chi.name, bot);
                    }
                }
            };

        // Wait until the channels are downloaded and start scanning them.
        final int expectedChannelDownloadDurationMs = 3000;
        final int delayBetweenChannels = 5000;
        executor.scheduleWithFixedDelay( scanJob, expectedChannelDownloadDurationMs, delayBetweenChannels, TimeUnit.MILLISECONDS);
    }
    
    
    /**
     *  Gets a list of users on given channel and updates info about their occurrences.
     */
    private void scanChannel( String channel, final IrcBotProxy bot ) {
        synchronized (this.channelsBeingScanned) {
            if( this.channelsBeingScanned.contains(channel) ){
                log.warn("  Already scanning channel: " + channel);
            }
            log.debug("  Scanning channel: " + channel);
            this.channelsBeingScanned.add(channel);
        }
        
        final Date now = new Date();
        UserListHandler handler = 
        new UserListHandlerBase() {
            public void onUserList( String channel, User[] users ) {
                for( User user : users ) {
                    whereIsService.updateUserInfo( channel, user, now );
                }
                //bot.partChannel(channel);
            }
        };
        bot.listUsersInChannel(channel, handler);
    }


    
    private static final DateFormat DF_TIME = new SimpleDateFormat("HH:mm");
    private static final DateFormat DF_DATE = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     *  Creates a string about where given user is.
     *  TODO: Relative time. I have it already in PasteBin plugin.
     */
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
            sb.append( ") " );
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