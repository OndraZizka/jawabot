package org.jboss.jawabot.plugin.logger.ent;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *  
 *  @author Ondrej Zizka
 */
@Entity
@Table(name="jb_chan_set", uniqueConstraints={
   @UniqueConstraint(columnNames="channel")
})
public class ChannelSettings implements Serializable {
   private static final long serialVersionUID = 1L;
   
   @Id @GeneratedValue
   private Long id;
   
   private String channel;

   @Column(name="log_on")
   private boolean loggingEnabled;

   
   
   
   
   public ChannelSettings() {
   }

   public ChannelSettings(String channel, boolean loggingEnabled) {
      this.channel = channel;
      this.loggingEnabled = loggingEnabled;
   }

      
   

   public Long getId() { return id; }
   public void setId(Long id) { this.id = id; }
   public String getChannel() { return channel; }
   public void setChannel(String channel) { this.channel = channel; }
   public boolean isLoggingEnabled() { return loggingEnabled; }
   public void setLoggingEnabled(boolean loggingEnabled) { this.loggingEnabled = loggingEnabled; }

   
   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final ChannelSettings other = (ChannelSettings) obj;
      if ((this.channel == null) ? (other.channel != null) : !this.channel.equals(other.channel)) {
         return false;
      }
      return true;
   }

   @Override
   public int hashCode() {
      int hash = 5;
      hash = 19 * hash + (this.channel != null ? this.channel.hashCode() : 0);
      return hash;
   }
   
}// class

