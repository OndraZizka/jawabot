
package org.jboss.jawabot.irc;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.jboss.jawabot.MailData;

/**
 * Carries reply to the command:
 *   What to reply to the channel, to the sender,
 *   whether the command had proper syntax,
 *   whether it was finished successfully.
 *
 * @author Ondrej Zizka
 */
public class CommandReply {

   public Boolean reportInvalidSyntax = false;
   public Boolean wasSuccessful = false;
   public boolean stateChanged = false;

   //public final List<String> replies = new LinkedList();
   //public final List<String> announcements = new LinkedList();

   public final List<CommandReplyMessage> ircMessages = new LinkedList();

   public final List<MailData> mailAnnouncements = new LinkedList();

   public final Set<String> additionalAnnounceChannels = new HashSet();
   public final Set<String> mailAnnounceRecipients = new HashSet();


   /** Const */
   public CommandReply() {
   }

   public String toString(){
      return "CommandReply{ "+ ircMessages.size() +" IRC msgs for "+additionalAnnounceChannels.size()+" ch, "
              + mailAnnouncements.size() +" mails for "+mailAnnounceRecipients.size()+" rcp }";
   }


   public void addReply( String str ){
      this.ircMessages.add( CommandReplyMessage.createReply(str) );
   }

   public void addAnnouncement( String str ){
      this.ircMessages.add(CommandReplyMessage.createAnnouncement(str) );
   }

   public void addBoth( String str ){
      this.ircMessages.add( new CommandReplyMessage(str) );
   }

}// class
