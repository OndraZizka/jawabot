package org.jboss.jawabot.irc.ent;


import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;


/**
 * Base entity for IRC events: Messages, joins, parts, actions, topic changes.
 * 
 * @author Ondrej Zizka
 */
@Entity
@Table(name="jw_irc_event")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TYPE", discriminatorType=DiscriminatorType.CHAR, length=1)
@DiscriminatorValue("E")
@NamedQuery(name="irc.events.byCriteria", query="SELECT ev FROM IrcEvent ev WHERE ev.channel = ? AND ev.when BETWEEN ? AND ?")
public class IrcEvent implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    
    @Id @GeneratedValue
    private Long id;

    private String server;
    private String channel;
    private String user;
    private String text;


   @Temporal(javax.persistence.TemporalType.TIMESTAMP)
   @Column(name="`when`")
   private Date when;
   
   public IrcEvent(){}
   public IrcEvent(String server, String channel, String user, String text, Date when) {
      this.server = server;
      this.channel = channel;
      this.user = user;
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
      return "IrcEvent{ #"+id+" " + user + "@ #" + channel + ": " + text + " }";
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) { return false;  }
      if (getClass() != obj.getClass()) { return false; }
      
      final IrcEvent other = (IrcEvent) obj;
      //if (this.id != other.id && (this.id == null || !this.id.equals(other.id)))
      //   return false;

      //if ((this.server == null) ? (other.server != null) : !this.server.equals(other.server))
      //   return false;
      
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
      //hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
      //hash = 47 * hash + (this.server != null ? this.server.hashCode() : 0);
      hash = 47 * hash + (this.user != null ? this.user.hashCode() : 0);
      hash = 47 * hash + (this.channel != null ? this.channel.hashCode() : 0);
      hash = 47 * hash + (this.when != null ? this.when.hashCode() : 0);
      return hash;
   }

    
}// class

