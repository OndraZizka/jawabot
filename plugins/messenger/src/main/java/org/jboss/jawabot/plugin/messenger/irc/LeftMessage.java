package org.jboss.jawabot.plugin.messenger.irc;

import java.util.Date;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.jboss.jawabot.irc.ent.IrcEvMessage;

/**
 *
 * @author Ondrej Zizka
 */
@Entity
@DiscriminatorValue("L")
public class LeftMessage extends IrcEvMessage {

		public LeftMessage() {
		}

		public LeftMessage(String server, String user, String channel, String text, Date when) {
				super(server, user, channel, text, when);
		}

		// Conversion - IrcEvMessage -> LeftMessage.
		public LeftMessage( IrcEvMessage msg ) {
				this( msg.getServer(), msg.getUser(), msg.getChannel(), msg.getText(), msg.getWhen() );
		}

		

}// class

