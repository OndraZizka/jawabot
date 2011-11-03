package org.jboss.jawabot.irc.ent;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *  
 *  @author Ondrej Zizka
 */
@Entity
@Table(name="jw_ircmsg")
public class IrcMessage implements Serializable {
   
   @Id @GeneratedValue
   private Long id;

   private String server;
   private String user;
   private String channel;
   private String text;
   
   @Temporal(javax.persistence.TemporalType.TIMESTAMP)
   @Column(name="`when`")
   private Date when;
   
   public IrcMessage(){}
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
   
   public Long getId() { return id; }
   public void setId(Long id) { this.id = id; }
   
   //</editor-fold>

   @Override
   public String toString() {
      return "IrcMessage{ " + user + "@ #" + channel + ": " + text + " }";
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) { return false;  }
      if (getClass() != obj.getClass()) { return false; }
      
      final IrcMessage other = (IrcMessage) obj;
      if (this.id != other.id && (this.id == null || !this.id.equals(other.id)))
         return false;

      if ((this.server == null) ? (other.server != null) : !this.server.equals(other.server))
         return false;
      
      if ((this.user == null) ? (other.user != null) : !this.user.equals(other.user))
         return false;
      
      if ((this.channel == null) ? (other.channel != null) : !this.channel.equals(other.channel))
         return false;
      
      if (this.when != other.when && (this.when == null || !this.when.equals(other.when)))
         return false;
      
      return true;
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
      hash = 47 * hash + (this.server != null ? this.server.hashCode() : 0);
      hash = 47 * hash + (this.user != null ? this.user.hashCode() : 0);
      hash = 47 * hash + (this.channel != null ? this.channel.hashCode() : 0);
      hash = 47 * hash + (this.when != null ? this.when.hashCode() : 0);
      return hash;
   }

   
   

   
   
}// class

