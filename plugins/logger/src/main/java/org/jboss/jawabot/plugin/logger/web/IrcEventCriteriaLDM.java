package org.jboss.jawabot.plugin.logger.web;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.apache.wicket.model.LoadableDetachableModel;
import org.jboss.jawabot.irc.ent.IrcEvent;
import org.jboss.jawabot.plugin.logger.ent.IrcEventsCriteria;
import org.jboss.weld.environment.se.jpa.JpaTransactional;

/**
 *  
 *  @author Ondrej Zizka
 */
@Dependent
public class IrcEventCriteriaLDM extends LoadableDetachableModel<List<IrcEvent>>
{
    @Inject EntityManager em;
    
    private IrcEventsCriteria crit;

    private IrcEventCriteriaLDM( IrcEventsCriteria crit ) {
        this.crit = crit;
    }
    
    /*@Produces public IrcEventCriteriaLDM create( IrcEventsCriteria crit ) {
        return new IrcEventCriteriaLDM( crit );
    }*/

    

    @Override
    @JpaTransactional
    protected List<IrcEvent> load() {
        return em.createQuery("SELECT ev FROM IrcEvent ev WHERE channel = ? AND when BETWEEN ? AND ?", IrcEvent.class)
                .setParameter( 1, this.crit.getChannel() )
                .setParameter( 2, this.crit.getSince() )
                .setParameter( 2, this.crit.getUntil() )
                .getResultList()
        ;
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

