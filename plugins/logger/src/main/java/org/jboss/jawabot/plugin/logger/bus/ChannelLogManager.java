package org.jboss.jawabot.plugin.logger.bus;

import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.apache.commons.lang.ObjectUtils;
import org.jboss.jawabot.irc.ent.IrcEvent;
import org.jboss.jawabot.plugin.logger.ent.IrcEventsCriteria;
import org.jboss.weld.environment.se.jpa.JpaTransactional;

/**
 * 
 * @author Ondrej Zizka
 */
public class ChannelLogManager {
    
    @Inject EntityManager em;
    
    
    
    @JpaTransactional
    public List<IrcEvent> getEventsByCriteria( IrcEventsCriteria crit )
    {
        return em.createQuery("SELECT ev FROM IrcEvent ev WHERE ev.channel = ? AND ev.when BETWEEN ? AND ?", IrcEvent.class)
                .setParameter( 1, crit.getChannel() )
                .setParameter( 2, crit.getSince() )
                .setParameter( 3, ObjectUtils.defaultIfNull( crit.getUntil(), new Date()) )
                .getResultList()
        ;
        
    }
    
}// class

