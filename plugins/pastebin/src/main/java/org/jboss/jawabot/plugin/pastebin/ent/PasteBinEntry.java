
package org.jboss.jawabot.plugin.pastebin.ent;

import cz.dynawest.util.DateUtils;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *  
 *  @author Ondrej Zizka
 */
@Entity
@Table(name="jb_pbin_entry")
public class PasteBinEntry implements Serializable {
   
   public static final PasteBinEntry NON_EXISTENT = new PasteBinEntry();

   private Long id;
   private String author;
   private String to;
   private String channel;
   private Date   when;
   private String text;

   
   
   public PasteBinEntry() {
      this.when = new Date();
   }

   
   public PasteBinEntry( String author, String text ) {
      this();
      this.setText(text);
      this.setAuthor(author);
   }
   
   
   
   

   @Id @GeneratedValue
   public Long getId() { return id; }
   public void setId(Long id) { this.id = id; }
   public String getAuthor() { return author; }
   public boolean hasAuthor() { return author != null; }
   public void setAuthor(String author) { this.author = author; }
   public String getChannel() { return channel; }
   public boolean hasChannel() { return channel != null; }
   public void setChannel(String channel) { this.channel = channel; }
   public String getText() { return text; }
   public void setText(String text) { assert null != (this.text = text); }
   @Temporal(TemporalType.TIMESTAMP)
   @Column(name="`when`")
   public Date getWhen() { return when; }
   public void setWhen(Date when) { this.when = when; }
   @Column(name="`for`")
   public String getFor() { return to; }
   public void setFor(String to) { this.to = to; }

   @Override
   public String toString() {
      return String.format("PasteBinEntry{ #%i by %s for %s in %s at %s }",
              id, author, to, channel, DateUtils.toStringSQL(when) );
   }



}// class
