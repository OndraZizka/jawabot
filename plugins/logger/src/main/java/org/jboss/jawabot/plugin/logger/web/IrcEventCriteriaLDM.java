package org.jboss.jawabot.plugin.logger.web;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.apache.wicket.model.LoadableDetachableModel;
import org.jboss.jawabot.irc.ent.IrcEvent;
import org.jboss.jawabot.plugin.logger.bus.ChannelLogManager;
import org.jboss.jawabot.plugin.logger.irc.IrcEventCriteria;



/**
 *  LoadableDetachableModel which loads IrcEvents based on given criteria.
 *  @author Ondrej Zizka
 * 
 *  TODO:  Don't use EM, use some IrcEventsManager or such.
 */
@Dependent
public class IrcEventCriteriaLDM extends LoadableDetachableModel<List<IrcEvent>>
{
    @Inject ChannelLogManager chanMan;
    
    
    private IrcEventCriteria crit;

    
    
    public IrcEventCriteriaLDM() {
    }

    private IrcEventCriteriaLDM( IrcEventCriteria crit ) {
        this.crit = crit;
    }
    

    
    @Override
    protected List<IrcEvent> load() {
        return this.chanMan.getEventsByCriteria( crit );
    }
    

    //<editor-fold defaultstate="collapsed" desc="get/set">
    public IrcEventCriteria getCrit() {
        return crit;
    }
    
    public void setCrit( IrcEventCriteria crit ) {
        this.crit = crit;
    }
    //</editor-fold>

    
}// class

