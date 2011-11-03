package org.jboss.jawabot.plugin.irc;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *  IRC Channel.
 *  Recognized by it's name and server.
 * 
 *  @author Ondrej Zizka
 */
@Entity
@Table(
        uniqueConstraints=@UniqueConstraint(columnNames={"name","server"})
)
public class Channel implements Serializable {
   
   @Id @GeneratedValue
   private long id;

   private String name;
   
   private String server;

   public Channel() {
   }

   
   
   Channel(String name) {
      this.name = name;
   }

   
   
   
   //<editor-fold defaultstate="collapsed" desc="get/set">
   public long getId() {
      return id;
   }
   
   public void setId(long id) {
      this.id = id;
   }
   
   public String getName() {
      return name;
   }
   
   public void setName(String name) {
      this.name = name;
   }
   
   public String getServer() {
      return server;
   }
   
   public void setServer(String server) {
      this.server = server;
   }
   //</editor-fold>

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final Channel other = (Channel) obj;
      if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
         return false;
      }
      if ((this.server == null) ? (other.server != null) : !this.server.equals(other.server)) {
         return false;
      }
      return true;
   }

   
   @Override
   public int hashCode() {
      int hash = 7;
      hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
      hash = 37 * hash + (this.server != null ? this.server.hashCode() : 0);
      return hash;
   }
   
   
}// class

