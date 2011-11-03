
package org.jboss.jawabot.irc;


/**
 * Carries the info about the command's context - who, where, when, etc.
 *
 * @author Ondrej Zizka
 */
public class CommandContext {

   public final String fromUser;
   
   public final String fromChannel;

   public final boolean isPrivate;

   /** Normalized user nick - the part without suffixes. */
   public final String fromUserNorm;


   /** Const */
   public CommandContext( String fromUser, String fromChannel ) {
      this.fromUser = fromUser;
      this.fromChannel = fromChannel;
      this.isPrivate = fromChannel == null;
      this.fromUserNorm = IrcUtils.normalizeUserNick( fromUser );
   }


   


}// class
