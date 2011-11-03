package org.jboss.jawabot.irc.ent;

import java.util.Date;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *  IRC event - Someone else joined a channel we're in.
 * 
 *   :ozizka!~ozizka@ozizka.brq.redhat.com JOIN :#some
 * 
 *  @author Ondrej Zizka
 */
@Entity
@DiscriminatorValue("J")
public class IrcEvJoin extends IrcEvent {

    public IrcEvJoin( String server, String channel, String user, String login, String hostname ) {
        super(server, channel, user, login+"@"+hostname, new Date());
    }

    public IrcEvJoin() {
    }
   
}// class

