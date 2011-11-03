package org.jboss.jawabot.irc;

public class CommandReplyMessage {

   final String text;
   final boolean isAnnouncement;
   final boolean isReply;

   public CommandReplyMessage(String text, boolean isAnnouncement, boolean isReply) {
      this.text = text;
      this.isAnnouncement = isAnnouncement;
      this.isReply = isReply;
   }

   public CommandReplyMessage(String text) {
      this.text = text;
      this.isAnnouncement = true;
      this.isReply = true;
   }

   public static CommandReplyMessage createAnnouncement(String text) {
      return new CommandReplyMessage(text, true, false);
   }

   public static CommandReplyMessage createReply(String text) {
      return new CommandReplyMessage(text, false, true);
   }


}// class
