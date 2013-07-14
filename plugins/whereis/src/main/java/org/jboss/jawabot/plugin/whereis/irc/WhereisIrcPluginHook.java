package org.jboss.jawabot.plugin.whereis.irc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
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
import org.jibble.pircbot.beans.User;
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
    private static final int MAX_CHANNELS_TO_SCAN = 10; // Make configurable.
    

    // TODO: Prevent multiple scanning over this.
    final Set<String> channelsBeingScanned = new ConcurrentSkipListSet();
    
    private volatile boolean shuttingDown;
    
    
    // IRC stuff.


    @Override
    public void onMessage( final IrcEvMessage msg, final IrcBotProxy bot ) throws IrcPluginException {
        if( ( ! msg.getPayload().startsWith("whereis")) && ! msg.getPayload().startsWith("seen") )
            return;
        
        // Remove "seen" or "whereis" from the beginning of msg.
        String pattern = StringUtils.removeStart( msg.getPayload(), "whereis").trim();
        pattern = StringUtils.removeStart( pattern, "seen").trim();
        
        
        // No wildcards -> search exact nick.
        if( !pattern.contains("*") ){
            List<SeenInfo> occurrences = this.whereIsService.whereIsUser( pattern, true );
            if( occurrences.isEmpty() ){
                bot.sendReplyTo( msg, "Sorry, no traces of "+pattern+".");
            }
            else{
                bot.sendReplyTo( msg, this.informAbout( pattern, occurrences ) );
            }
        }
        // Wildcards, list all matching nicks.
        else{
            Map<String, Set<SeenInfo>> users = this.whereIsService.searchUser( pattern, true );
            if( users.isEmpty() ){
                bot.sendReplyTo( msg, "Sorry, no traces of "+pattern+".");
            }
            else{
                for( Map.Entry<String, Set<SeenInfo>> entry : users.entrySet() ) {
                    bot.sendReplyTo( msg, this.informAbout( entry.getKey(), new ArrayList(entry.getValue()) ) );
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
        this.scanChannelWeAreIn( channel, bot );
    }


    @Override
    public void onUserList( String channel, User[] users, IrcBotProxy bot ) {
        this.whereIsService.updateUsersInfo( channel, Arrays.asList( users ) );
    }
    


    /**
     *  Scan all channels of the server on connect.
     */
    @Override
    public void onConnect( final IrcBotProxy bot )
    {
        log.info(" onConnect();  Will now scan all channels.");
        this.shuttingDown = false;
        
        // Create a queue for channels, sorted by user count.
        final PriorityQueue<ChannelInfo> scanQueue = new PriorityQueue<ChannelInfo>(800, ChannelInfo.USER_COUNT_COMPARATOR );
        
        ChannelInfoHandler handler = new ChannelInfoHandler() {
            public void onChannelInfo( String channel, int userCount, String topic ) {
                //scanChannel( channel, bot );
                if( userCount >= MIN_USER_COUNT_TO_SCAN_CHANNEL )
                    scanQueue.add( new ChannelInfo(channel, userCount, topic) );
            }
        };
        bot.listChannels( handler );
        
        
        // Schedule channel scanning in other thread.
        {
            final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            Runnable scanJob =
                new Runnable() {
                    // Limit to few biggest channels, by number.
                    int countDown = MAX_CHANNELS_TO_SCAN; // TODO: Read from options.

                    public void run() {
                        if( WhereisIrcPluginHook.this.shuttingDown ){
                            executor.shutdown();
                            return;
                        }
                        
                        ChannelInfo chi = scanQueue.poll();
                        if( null == chi ){
                            logScan.debug("  No more channels in scan queue, stopping. ");
                            executor.shutdown();
                        }
                        else if( countDown-- == 0 ){
                            logScan.debug("  Maximum # of channels was scanned, stopping. ");
                            executor.shutdown();
                        }
                        else {
                            logScan.debug("  Scanning " + chi.toString() );
                            scanChannelWeAreNotIn(chi.name, bot);
                        }
                    }
                };

            // Wait until the channels are downloaded and start scanning them.
            final int expectedChannelDownloadDurationMs = 4000;
            final int delayBetweenChannels = 2000;
            log.debug("Launching executor with scanJob for every " + delayBetweenChannels + " seconds.");
            executor.scheduleWithFixedDelay( scanJob, expectedChannelDownloadDurationMs + 1000, delayBetweenChannels, TimeUnit.MILLISECONDS);
        }
        

        // 2nd executor - regularly scan channels we are in.
        // This should be only needed at the beginning as we react to onJoin() and onBotJoin().
        {
            final ScheduledExecutorService executor2 = Executors.newSingleThreadScheduledExecutor();
            
            Runnable jobScanChannelsWeAreIn =
                new Runnable() {
                    public void run() {
                        if( WhereisIrcPluginHook.this.shuttingDown ){
                            log.debug("Stopping jobScanChannelsWeAreIn.");
                            executor2.shutdown();
                            return;
                        }
                        
                        log.debug("Scanning all channels we are in.");
                        for( String channel : bot.getChannels() ){
                            whereIsService.updateUsersInfo( channel, bot.getUsers( channel ) );
                        }
                    }
                };

            final int SCAN_INTERVAL = 60;
            log.debug("Launching executor with jobScanChannelsWeAreIn for every " + SCAN_INTERVAL + " seconds.");
            //executor2.scheduleWithFixedDelay( jobScanChannelsWeAreIn, 4, SCAN_INTERVAL, TimeUnit.SECONDS);
        }
    }


    @Override
    public void onDisconnect( IrcBotProxy pircBotProxy ) {
        this.shuttingDown = true;
    }
    
    
    
    
    /**
     *  Gets a list of users on given channel and updates info about their occurrences.
     */
    private void scanChannelWeAreIn( String channel, final IrcBotProxy bot ) {
        this.whereIsService.updateUsersInfo( channel, bot.getUsers( channel ) );
    }
    
    
    /**
     *  Gets a list of users on given channel and updates info about their occurrences.
     */
    private void scanChannelWeAreNotIn( String channel, final IrcBotProxy bot ) {
        synchronized (this.channelsBeingScanned) {
            if( this.channelsBeingScanned.contains(channel) ){
                log.warn("  Already scanning channel: " + channel);
                return;
            }
            log.debug("  Scanning channel: " + channel);
            this.channelsBeingScanned.add(channel);
        }
        
        // Assynchronously scan the channel users.
        UserListHandler handler = 
        new UserListHandlerBase() {
            public void onUserList( String channel, User[] users ) {
                whereIsService.updateUsersInfo( channel, Arrays.asList( users ) );
                WhereisIrcPluginHook.this.channelsBeingScanned.remove(channel);
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
        
        StringBuilder sb = new StringBuilder(" User " + nick + " seen in ");
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


    @Override public String toString() {
        return "#" + name + "("+userCount+")";
    }

    
    public static final 
        Comparator<ChannelInfo> USER_COUNT_COMPARATOR = new Comparator<ChannelInfo>() {
            public int compare( ChannelInfo o1, ChannelInfo o2 ) {
                return o2.userCount - o1.userCount; // Bigger channels first.
            }
        };
    
}