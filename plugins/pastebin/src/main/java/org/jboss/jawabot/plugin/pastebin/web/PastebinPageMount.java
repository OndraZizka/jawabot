package org.jboss.jawabot.plugin.pastebin.web;

import cz.dynawest.wicket.NonVersionedHybridUrlCodingStrategy;
import org.jboss.jawabot.mod.web.IPageMount;
import org.jboss.jawabot.mod.web.MountProxy;
import org.jboss.jawabot.plugin.pastebin.web._pg.PasteBinPage;
import org.jboss.jawabot.plugin.pastebin.web._pg.PasteBinShowPage;

/**
 *  Mounts Logger pages into Wicket.
 *  @author Ondrej Zizka
 */
public class PastebinPageMount implements IPageMount {

    @Override
    public void mount( MountProxy wicketApp ) {
       
        wicketApp.mountBookmarkablePage("pastebin/new", PasteBinPage.class);
        wicketApp.mount(new NonVersionedHybridUrlCodingStrategy("pastebin", PasteBinShowPage.class));

    }
    
}// class

