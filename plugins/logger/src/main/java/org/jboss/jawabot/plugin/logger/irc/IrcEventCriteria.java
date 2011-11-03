package org.jboss.jawabot.plugin.logger.irc;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.jboss.jawabot.irc.ent.IrcEvAction;
import org.jboss.jawabot.irc.ent.IrcEvJoin;
import org.jboss.jawabot.irc.ent.IrcEvMessage;
import org.jboss.jawabot.irc.ent.IrcEvPart;
import org.jboss.jawabot.irc.ent.IrcEvTopic;
import org.jboss.jawabot.irc.ent.IrcEvent;

/**
 *
 * @author Ondrej Zizka
 */
public class IrcEventCriteria {
   
   private String channel;
   private String user;
   private Date since;
   private Date until;
   private int  num;
   private Set<Class<? extends IrcEvent>>  types;

   
   public IrcEventCriteria(String channel) {
      this.channel = channel;
   }

    public IrcEventCriteria(String channel, Date since, Date until) {
        this.channel = channel;
        this.since = since;
        this.until = until;
    }
   
   
   //<editor-fold defaultstate="collapsed" desc="get/set">
   public String getChannel() {
        return channel;
    }

    public IrcEventCriteria setChannel(String channel) {
        this.channel = channel;
        return this;
    }

    public Date getFromDate() {
        return since;
    }

    public IrcEventCriteria setFromDate(Date fromDate) {
        this.since = fromDate;
        return this;
    }

    public int getNum() {
        return num;
    }

    public IrcEventCriteria setNum(int num) {
        this.num = num;
        return this;
    }

    public Date getToDate() {
        return until;
    }

    public IrcEventCriteria setToDate(Date toDate) {
        this.until = toDate;
        return this;
    }

    public String getUser() {
        return user;
    }

    public IrcEventCriteria setUser(String user) {
        this.user = user;
        return this;
    }

    public Set<Class<? extends IrcEvent>> getTypes() {
        return types;
    }

    public IrcEventCriteria setTypes(Set<Class<? extends IrcEvent>> types) {
        this.types = types;
        return this;
    }

    public IrcEventCriteria setAllTypes() {
        
        this.types = new HashSet(5);
        this.types.add( IrcEvMessage.class );
        this.types.add( IrcEvAction.class );
        this.types.add( IrcEvJoin.class );
        this.types.add( IrcEvPart.class );
        this.types.add( IrcEvTopic.class );
        return this;
    }

    public IrcEventCriteria addType(Class<? extends IrcEvent> type) {
        this.types.add(type);
        return this;
    }

    public Date getSince() {
        return since;
    }

    public void setSince(Date since) {
        this.since = since;
    }

    public Date getUntil() {
        return until;
    }

    public void setUntil(Date until) {
        this.until = until;
    }
   
    
   //</editor-fold>

   @Override
   public String toString() {
      return "IrcEventCriteria{" + "#" + channel + ", user=" + user + ", from=" + since + ", to=" + until + ", num=" + num + '}';
   }
   
}// class

