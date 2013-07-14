package org.jboss.jawabot.plugin.whereis.irc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.jibble.pircbot.beans.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  
 *  @author Ondrej Zizka
 */
public class MemoryWhereIsService
{
    private static final Logger log = LoggerFactory.getLogger( MemoryWhereIsService.class );

    
    // We need to have two maps - channelsToUsers is to delete the old entries.
    private final Map<String, Set<SeenInfo>> userToChannels = new HashMap();
    private final Map<String, Set<SeenInfo>> channelsToUsers = new HashMap();

    
    synchronized void updateUserInfo( String channel, User user, Date now ) {
        this.updateUserInfo( channel, user.getNick(), now );
    }
    
    synchronized void updateUsersInfo( String channel, List<User> users ) {
        final Date now = new Date();
        for( User user : users ) {
            this.updateUserInfo( channel, user, now );
        }
    }
    
    synchronized void updateUserInfo( String channel, String nick, Date now ) {
        if( log.isTraceEnabled() )  log.trace("   Updating info about user: " + nick );
        
        // User -> channels/when mapping.
        {
            Set<SeenInfo> seenInfos = this.userToChannels.get( nick );
            if( null == seenInfos )
                this.userToChannels.put( nick, seenInfos = new HashSet() );

            seenInfos.add( new SeenInfo(channel, now) );
        }
        
        // Channel -> users/when mapping.
        {
            Set<SeenInfo> seenInfos = this.channelsToUsers.get( channel );
            if( null == seenInfos )
                this.channelsToUsers.put( channel, seenInfos = new HashSet() );

            seenInfos.add( new SeenInfo(nick, now) );
        }
        
    }
    

    /**
     * @returns  A list of occurrences of given user, with time info, sorted by time.
     */
    public List<SeenInfo> whereIsUser( String nick ){
        Set<SeenInfo> seenInfos = this.userToChannels.get(nick);
        if( null == seenInfos )
            return Collections.EMPTY_LIST;
        List list = new ArrayList(seenInfos);
        Collections.sort( list, SeenInfo.WHEN_COMPARATOR );
        return list;
    }

    
    /**
     * @returns  A list of occurrences of given user, with time info, sorted by time.
     * @param  nickPattern   Wildcard pattern like  "ozizka*" or "*swaite*"
     */
    public Map<String, Set<SeenInfo>> searchUser( String nickPattern ){
        Map<String, Set<SeenInfo>> foundUsers = new HashMap<String, Set<SeenInfo>>();
        
        Pattern pat = Pattern.compile( nickPattern.replace("*", ".*") );
        
        for( String user : this.userToChannels.keySet() ) {
            if( pat.matcher(user).matches() )
                foundUsers.put(user, this.userToChannels.get(user));
        }
        
        return foundUsers;
    }

}// class

