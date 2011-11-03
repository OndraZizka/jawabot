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

    public IrcEvNickChange( String server, String user, String newNick, Date when ) {
        super( server, null, user, newNick, when );
    }

    public IrcEvNickChange() {
    }
   
}// class

