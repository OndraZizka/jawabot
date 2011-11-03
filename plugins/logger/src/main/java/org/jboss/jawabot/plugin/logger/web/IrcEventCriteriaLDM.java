package org.jboss.jawabot.plugin.logger.web;

import java.util.Date;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.apache.commons.lang.ObjectUtils;
import org.apache.wicket.model.LoadableDetachableModel;
import org.jboss.jawabot.irc.ent.IrcEvent;
import org.jboss.jawabot.plugin.logger.bus.ChannelLogManager;
import org.jboss.jawabot.plugin.logger.ent.IrcEventsCriteria;
import org.jboss.weld.environment.se.jpa.JpaTransactional;

/**
 *  LoadableDetachableModel which loads IrcEvents based on given criteria.
 *  @author Ondrej Zizka
 * 
 *  TODO:  Don't use EM, use some IrcEventsManager or such.
 */
@Dependent
public class IrcEventCriteriaLDM extends LoadableDetachableModel<List<IrcEvent>>
{
    //@Inject EntityManager em;
    
    @Inject ChannelLogManager chanMan;
    
    
    private IrcEventsCriteria crit;

    
    
    public IrcEventCriteriaLDM() {
    }

    private IrcEventCriteriaLDM( IrcEventsCriteria crit ) {
        this.crit = crit;
    }
    

    
    @Override
    //@JpaTransactional
    protected List<IrcEvent> load() {
        /*
        return em.createQuery("SELECT ev FROM IrcEvent ev WHERE channel = ? AND when BETWEEN ? AND ?", IrcEvent.class)
                .setParameter( 1, this.crit.getChannel() )
                .setParameter( 2, this.crit.getSince() )
                .setParameter( 3, ObjectUtils.defaultIfNull( this.crit.getUntil(), new Date()) )
                .getResultList()
        ;
         */
        return this.chanMan.getEventsByCriteria( crit );
    }
    

    //<editor-fold defaultstate="collapsed" desc="get/set">
    public IrcEventsCriteria getCrit() {
        return crit;
    }
    
    public void setCrit(IrcEventsCriteria crit) {
        this.crit = crit;
    }
    //</editor-fold>

    
}// class

