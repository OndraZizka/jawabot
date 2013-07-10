package org.jboss.jawabot.irc.ent;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import org.jboss.jawabot.irc.IrcUtils;


/**
 *  A message, adds receiver property and payload to IrcEvent.
 * 
 * Those parts follow the typical formatting of an IRC message for someone.
 * 
 *    <recipient>: <payload>
 * 
 *  @author Ondrej Zizka
 */
@Entity
@DiscriminatorValue("M")
@Access(AccessType.PROPERTY)
public class IrcEvMessage extends IrcEvent {

    public IrcEvMessage( String server, String channel, String fromUser, String text, Date when ) {
        super( server, channel, fromUser, text, when );
        this.parseRecipientAndPayload();
    }

    public IrcEvMessage() {
    }
    
    
    protected List<String> recipients = null;
    protected String       payload    = null;

    
    private void parseRecipientAndPayload() {
        //List<String> recp = IrcUtils.whoIsThisMsgFor( this.getText() );
        //if( ! recp.isEmpty() )
        //    this.setRecipient( recp.get(0) );
        List<String> parts = IrcUtils.parsePayloadAndRecipients( this.getText(), false );
        this.payload = parts.get(0);
        this.recipients = parts.subList( 1, parts.size() );
    }



    //<editor-fold defaultstate="collapsed" desc="get/set">
    @Transient
    public List<String> getRecipients() { return recipients; }
    
    @Transient
    public String getPayload() { return payload; }
        
    public String getRecipient() {
        return recipients.get(0);
    }
    
    public void setRecipient(String nick) {
        this.recipients = Collections.singletonList(nick);
    }
    //</editor-fold>

}// class
