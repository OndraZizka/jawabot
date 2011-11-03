
package org.jboss.jawabot.irc;

/**
 *
 *
 * @author Ondrej Zizka
 */
public interface CommandHandler {

   /**
    * @param ctx     Command context - which channel, which user, normalized nick, is private msg?
    * @param params  Parameters string of the command, in the original case (as recv'd from IRC).
    * @return  What to reply, what and where to announce, what to send to which mails, success flags.
    */
    
   public CommandReply onJoin( CommandContext ctx, String params );

   public CommandReply onPleaseLeave( CommandContext ctx, String params );

   public CommandReply onQuit( CommandContext ctx, String params );

   public CommandReply onHelp( CommandContext ctx, String params );

}// class
