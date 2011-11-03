package org.jboss.jawabot.irc.ent;

import java.util.Date;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *  
 *  @author Ondrej Zizka
 */
@Entity
@DiscriminatorValue("P")
public class IrcEvPart extends IrcEvent {

    public IrcEvPart(String server, String user, String channel, String text, Date when) {
        super(server, user, channel, text, when);
    }

    public IrcEvPart() {
    }
   
}// class

