package org.jboss.jawabot.irc.ent;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *  
 *  @author Ondrej Zizka
 */
@Entity
@Table(name="jw_ircuser")
public class IrcUser {
   
   @Id private long id;
   
   private String name;
   
   private String host;

   
   public long getId() { return id; }
   public void setId(long id) { this.id = id; }
   public String getName() { return name; }
   public void setName(String name) { this.name = name; }
   public String getHost() { return host; }
   public void setHost(String host) { this.host = host; }
   
}// class

