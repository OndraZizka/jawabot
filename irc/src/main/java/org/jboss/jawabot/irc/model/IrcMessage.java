package org.jboss.jawabot.irc.model;

import java.io.Serializable;
import java.util.Date;

/**
 *  
 *  @author Ondrej Zizka
 */
public class IrcMessage implements Serializable {
   
   private String server;
   private String user;
   private String channel;
   private String text;
   private Date when;

   
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

   
   
   
}// class

