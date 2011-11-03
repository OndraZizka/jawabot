package org.jboss.jawabot.irc.ent;

import java.util.Date;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *  
 *  @author Ondrej Zizka
 */
@Entity
@DiscriminatorValue("T")
public class IrcEvTopic extends IrcEvent {

    public IrcEvTopic(String server, String user, String channel, String text, Date when) {
        super(server, user, channel, text, when);
    }

    public IrcEvTopic() {
    }
   
}// class

