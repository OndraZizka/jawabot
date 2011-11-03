package org.jboss.jawabot.plugin.logger.web;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.apache.wicket.model.LoadableDetachableModel;
import org.jboss.jawabot.irc.ent.IrcEvent;
import org.jboss.jawabot.plugin.logger.bus.ChannelLogManager;
import org.jboss.jawabot.plugin.logger.ent.IrcEventsCriteria;

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
    
    
    private IrcEventsCriteria crit;

    
    
    public IrcEventCriteriaLDM() {
    }

    private IrcEventCriteriaLDM( IrcEventsCriteria crit ) {
        this.crit = crit;
    }
    

    
    @Override
    protected List<IrcEvent> load() {
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

