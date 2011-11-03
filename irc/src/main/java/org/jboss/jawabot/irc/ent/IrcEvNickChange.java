package org.jboss.jawabot.irc.ent;

import java.util.Date;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *  
 *  @author Ondrej Zizka
 */
@Entity
@DiscriminatorValue("N")
public class IrcEvNickChange extends IrcEvent {

    public IrcEvNickChange( String server, String oldNick, String newNick, String login, String hostname, Date when ) {
        super( server, null, oldNick, newNick, when );
    }

    public IrcEvNickChange() {
    }

    
    
    // We're re-using the text field. Is that a good idea?
    public String getNewNick() {
        return this.text;
    }

    public void setNewNick(String newNick) {
        this.text = newNick;
    }
    
    
    
    /**  Nick change doesn't have text. Discards what's passed.
     *   @returns null.
     */
    @Override
    public String getText() {
        return null;
    }

    
    /**  Nick change doesn't have text. Discards what's passed. */
    @Override
    public void setText(String text) {
        //super.setText(text);
    }

    
    @Override
    public String toString() {
        return "IrcEvNickChange{ " + this.getUser() + " -> " + this.getNewNick() + " @ " + this.getChannel() + "}";
    }
    
    
   
}// class

