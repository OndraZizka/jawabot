package org.jboss.jawabot.irc.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;

/**
 *  
 *  @author Ondrej Zizka
 */
@Entity
public class IrcMessage implements Serializable {
   
   private String server;
   private String user;
   private String channel;
   private String text;
   private Date when;

   public IrcMessage(String server, String user, String channel, String text, Date when) {
      this.server = server;
      this.user = user;
      this.channel = channel;
      this.text = text;
      this.when = when;
   }

  
   //<editor-fold defaultstate="collapsed" desc="get/set">
   public String getChannel() {
      return channel;
   }
   
   public void setChannel(String channel) {
      this.channel = channel;
   }
   
   public String getText() {
      return text;
   }
   
   public void setText(String text) {
      this.text = text;
   }
   
   public String getUser() {
      return user;
   }
   
   public void setUser(String user) {
      this.user = user;
   }
   
   public Date getWhen() {
      return when;
   }
   
   public void setWhen(Date when) {
      this.when = when;
   }
   
   public String getServer() {
      return server;
   }

   public void setServer(String server) {
      this.server = server;
   }
   //</editor-fold>

   @Override
   public String toString() {
      return "IrcMessage{ " + user + "@ #" + channel + ": " + text + " }";
   }

   
   
}// class

