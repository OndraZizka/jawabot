package org.jboss.jawabot.irc.ent;

import java.util.Date;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
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
   
}// class

