package org.jboss.jawabot.irc.ent;

import java.util.Date;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *  
 *  @author Ondrej Zizka
 */
@Entity
@DiscriminatorValue("A")
public class IrcEvAction extends IrcEvent {

    public IrcEvAction( String server, String channel, String user, String text, Date when ) {
        super( server, channel, user, text, when );
    }

    public IrcEvAction() {
    }
   
}// class

