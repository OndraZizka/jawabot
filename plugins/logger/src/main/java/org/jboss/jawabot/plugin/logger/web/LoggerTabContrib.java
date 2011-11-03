package org.jboss.jawabot.plugin.logger.web;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.jboss.jawabot.mod.web.ITabBarContrib;
import org.jboss.jawabot.plugin.logger.web._pg.LoggerChannelsListPage;

/**
 *  
 *  @author Ondrej Zizka
 */
public class LoggerTabContrib implements ITabBarContrib {

    @Override
    public Label getLabel( String id ) {
        return new Label( id, "Logged channels");
    }

    @Override
    public Class<? extends WebPage> getLinkedPage() {
        return LoggerChannelsListPage.class;
    }
    
    
    
}// class

