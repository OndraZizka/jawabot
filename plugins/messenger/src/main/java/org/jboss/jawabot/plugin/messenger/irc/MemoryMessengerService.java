package org.jboss.jawabot.plugin.messenger.irc;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jboss.jawabot.irc.ent.IrcEvMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  
 *  @author Ondrej Zizka
 */
public class MemoryMessengerService
{
    private static final Logger log = LoggerFactory.getLogger( MemoryMessengerService.class );

    
    private final Map<String, List<LeftMessage>> userToMessages = new HashMap();

    
    public synchronized void leaveMessage( IrcEvMessage msg ) {
        log.debug("   Leaving message for user: " + msg.getRecipient() );
        assert( msg.getRecipient() != null );

        List<LeftMessage> userMessages = this.userToMessages.get( msg.getRecipient() );
        if( null == userMessages )
            this.userToMessages.put( msg.getRecipient(), userMessages = new LinkedList() );

        userMessages.add( new LeftMessage( msg ) );
        
    }
    

    /**
     * @returns  A list of occurrences of given user, with time info, sorted by time.
     */
    public List<LeftMessage> getMessagesForUser( String nick, boolean delete ){
        List<LeftMessage> leftMessages = this.userToMessages.get(nick);
        if( null == leftMessages )
            return Collections.EMPTY_LIST;

        if( delete )
                this.userToMessages.put( nick, new LinkedList<LeftMessage>() );
            
        return leftMessages;
    }

    
    /**
     *  Removes those recipients which are blocked from receiving messages.
     *  Currently static; could be configurable on runtime in the future.
     * @param recipients 
     */
    void removeBlockedRecipients( List<String> recipients ) {
        recipients.remove("http");
        recipients.remove("https");
        recipients.remove("ftp");
        recipients.remove("mailto");
        recipients.remove("scp");
    }

    
}// class

