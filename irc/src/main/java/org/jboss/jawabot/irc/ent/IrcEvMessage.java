package org.jboss.jawabot.irc.ent;

import java.util.Date;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.jboss.jawabot.irc.IrcUtils;


/**
 *  A message, adds receiver property to IrcEvent.
 * 
 *  @author Ondrej Zizka
 */
@Entity
@DiscriminatorValue("M")
public class IrcEvMessage extends IrcEvent {

    public IrcEvMessage( String server, String channel, String user, String text, Date when ) {
        super( server, channel, user, text, when );
        this.initRecipient();
    }

    public IrcEvMessage() {
    }
    
    
    protected String recipient = null;

    
    private void initRecipient() {
        List<String> recp = IrcUtils.whoIsThisMsgFor( this.getText() );
        if( ! recp.isEmpty() )
            this.setRecipient( recp.get(0) );
    }
   
    

    //<editor-fold defaultstate="collapsed" desc="get/set">
    public String getRecipient() {
        return recipient;
    }
    
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    //</editor-fold>

}// class

