
package org.jboss.jawabot.plugin.reserv.irc;

import org.jboss.jawabot.irc.CommandContext;
import org.jboss.jawabot.irc.CommandReply;


/**
 *
 *
 * @author Ondrej Zizka
 */
public interface ReservationCommandHandler {

   /**
    * @param ctx     Command context - which channel, which user, normalized nick, is private msg?
    * @param params  Parameters string of the command, in the original case (as recv'd from IRC).
    * @return  What to reply, what and where to announce, what to send to which mails, success flags.
    */
   public CommandReply onTake( CommandContext ctx, String params );

   public CommandReply onFind( CommandContext ctx, String params );

   public CommandReply onLeave( CommandContext ctx, String params );

   public CommandReply onList( CommandContext ctx, String params );

}// class
