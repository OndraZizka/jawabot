package org.jboss.jawabot.plugin.pastebin.web;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.jboss.jawabot.mod.web.ITabBarContrib;
import org.jboss.jawabot.plugin.pastebin.web._pg.PasteBinPage;

/**
 *  
 *  @author Ondrej Zizka
 */
public class PastebinTabContrib implements ITabBarContrib {

    @Override
    public Label getLabel( String id ) {
        return new Label( id, "PasteBin");
    }

    @Override
    public Class<? extends WebPage> getLinkedPage() {
        return PasteBinPage.class;
    }
    
    
    
}// class

