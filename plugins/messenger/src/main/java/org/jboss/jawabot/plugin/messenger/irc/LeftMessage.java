package org.jboss.jawabot.plugin.whereis.irc;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.jboss.jawabot.irc.ent.IrcEvMessage;

/**
 *
 * @author Ondrej Zizka
 */
@Entity
@DiscriminatorValue("L")
public class LeftMessage extends IrcEvMessage {

}// class

