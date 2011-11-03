package org.jboss.jawabot.plugin.logger.ent;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Temporal;

/**
 *   Helper class for an aggregate query in ChannelLogManager.
 *  @author Ondrej Zizka
 */
//@Entity
public class ChannelLogInfo implements Serializable {
    
    String name;
    
    Long count;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    Date first;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    Date last;

    public ChannelLogInfo(String name, Long count, Date first, Date last) {
        this.name = name;
        this.count = count;
        this.first = first;
        this.last = last;
    }
    
    
    
    //<editor-fold defaultstate="collapsed" desc="get/set">
    public Long getCount() {
        return count;
    }
    
    public void setCount(Long count) {
        this.count = count;
    }
    
    public Date getFirst() {
        return first;
    }
    
    public void setFirst(Date first) {
        this.first = first;
    }
    
    public Date getLast() {
        return last;
    }
    
    public void setLast(Date last) {
        this.last = last;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    //</editor-fold>
    
}// class

