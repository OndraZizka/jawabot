package org.jboss.jawabot.irc.ent;

import java.util.Date;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


/**
 *  A message, adds receiver property to IrcEvent.
 * 
 *  @author Ondrej Zizka
 */
@Entity
@DiscriminatorValue("M")
public class IrcEvMessage extends IrcEvent {

    public IrcEvMessage(String server, String user, String channel, String text, Date when) {
        super(server, user, channel, text, when);
    }

    public IrcEvMessage() {
    }
    
    
    
    protected String recipient = null;

    //<editor-fold defaultstate="collapsed" desc="get/set">
    public String getRecipient() {
        return recipient;
    }
    
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    //</editor-fold>
   
}// class

