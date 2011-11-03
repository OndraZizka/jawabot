
package org.jboss.jawabot.pastebin;

import cz.dynawest.util.DateUtils;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *  
 *  @author Ondrej Zizka
 */
@Entity
public class PasteBinEntry {

   private Long id;
   private String by;
   private String to;
   private String channel;
   private Date   when;
   private String text;

   @Id
   public Long getId() { return id; }
   public void setId(Long id) { this.id = id; }
   public String getBy() { return by; }
   public void setBy(String by) { this.by = by; }
   public String getChannel() { return channel; }
   public void setChannel(String channel) { this.channel = channel; }
   public String getText() { return text; }
   public void setText(String text) { this.text = text; }
   @Temporal(TemporalType.TIMESTAMP)
   public Date getWhen() { return when; }
   public void setWhen(Date when) { this.when = when; }
   public String getFor() { return to; }
   public void setFor(String to) { this.to = to; }

   @Override
   public String toString() {
      return String.format("PasteBinEntry{ #%i by %s for %s in %s at %s }",
              id, by, to, channel, DateUtils.toStringSQL(when) );
   }



}// class
