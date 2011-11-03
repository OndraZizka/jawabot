package org.jboss.jawabot.plugin.logger.ent;

import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *  Not an entity - not needed yet.
 *  @author Ondrej Zizka
 */
public class IrcEventsCriteria {
    
    @Id @GeneratedValue
    private Long id;
    
    private String channel;
    private Date since;
    private Date until;

    
    public IrcEventsCriteria() {
    }

    public IrcEventsCriteria(String channel, Date since, Date until) {
        this.channel = channel;
        this.since = since;
        this.until = until;
    }

    
    
    //<editor-fold defaultstate="collapsed" desc="get/set">
    public String getChannel() {
        return channel;
    }
    
    public void setChannel(String channel) {
        this.channel = channel;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    

}// class

