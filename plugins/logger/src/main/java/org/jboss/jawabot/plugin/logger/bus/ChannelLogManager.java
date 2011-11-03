package org.jboss.jawabot.plugin.logger.bus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.apache.commons.lang.ObjectUtils;
import org.jboss.jawabot.irc.ent.IrcEvent;
import org.jboss.jawabot.plugin.logger.ent.ChannelLogInfo;
import org.jboss.jawabot.plugin.logger.irc.IrcEventCriteria;
import org.jboss.weld.environment.se.jpa.JpaTransactional;

/**
 * 
 * @author Ondrej Zizka
 */
public class ChannelLogManager {
    
    @Inject EntityManager em;
    
    
    
    public List<IrcEvent> getEventsByCriteria( IrcEventCriteria crit ){
        return getEventsByCriteria(crit, 0, Integer.MAX_VALUE, false);
    }

    public List<IrcEvent> getEventsByCriteria( IrcEventCriteria crit, boolean descending ){
        return getEventsByCriteria(crit, 0, Integer.MAX_VALUE, descending);
    }
    
    @JpaTransactional
    public List<IrcEvent> getEventsByCriteria( IrcEventCriteria crit, int first, int count, boolean descending ) {
        String desc = descending ? "DECS" : "";
        TypedQuery<IrcEvent> query = 
                em.createQuery("SELECT ev FROM IrcEvent ev WHERE ev.channel = ? AND ev.when BETWEEN ? AND ? ORDER BY id " + desc, IrcEvent.class)
                .setParameter( 1, crit.getChannel() )
                .setParameter( 2, crit.getFromDate() )
                .setParameter( 3, ObjectUtils.defaultIfNull( crit.getToDate(), new Date()) );
        if( first != 0 )                   query.setFirstResult(first);
        if( count != Integer.MAX_VALUE )   query.setMaxResults(count);
        return query.getResultList();
    }
    
    
    /**  @returns  Number of events conforming given criteria. */
    @JpaTransactional
    public int getEventsCountByCriteria( IrcEventCriteria crit ) {
        return em.createQuery("SELECT COUNT(*) FROM IrcEvent ev WHERE ev.channel = ? AND ev.when BETWEEN ? AND ?", Integer.class)
                .setParameter( 1, crit.getChannel() )
                .setParameter( 2, crit.getFromDate() )
                .setParameter( 3, ObjectUtils.defaultIfNull( crit.getToDate(), new Date()) )
                .getSingleResult();
    }
    
    
    @JpaTransactional
    public List<String> getLoggedChannelNames(){
        return em.createQuery("SELECT ev.channel FROM IrcEvent ev GROUP BY ev.channel ORDER BY ev.channel", String.class).getResultList();
    }
    
    @JpaTransactional
    public List<ChannelLogInfo> getLoggedChannelInfos(){
        /*return em.createQuery("SELECT NEW " + ChannelLogInfo.class.getName() +
        "( ev.channel, COUNT(*) AS count, MIN(ev.when) AS  first, MAX(ev.when) AS last )"
        + " FROM IrcEvent ev GROUP BY ev.channel ORDER BY ev.channel", ChannelLogInfo.class).getResultList();/*/
        
        /*List<Object[]> list = em.createQuery("SELECT ev.channel, COUNT(*) AS count, MIN(ev.when) AS  first, MAX(ev.when) AS last"
                                            + " FROM IrcEvent ev GROUP BY ev.channel ORDER BY ev.channel", Object[].class).getResultList();
        for( Object[] row : list ) {
            ret.add( new ChannelLogInfo( (String)row[0], (Long)row[1], (Date)row[2], (Date)row[3]) );
        }/**/
        
        // TODO:  Fix when https://hibernate.onjira.com/browse/HHH-5348 gets fixed.
        List<String> list = em.createQuery("SELECT ev.channel FROM IrcEvent ev GROUP BY ev.channel ORDER BY ev.channel", String.class).getResultList();
        List<ChannelLogInfo> ret = new ArrayList(list.size());
        for( String row : list ) {
            ret.add( new ChannelLogInfo( row, 999L, new Date(), new Date()) );
        }
        return ret;
    }

}// class

