package org.jboss.jawabot.plugin.reserv.web;

import org.apache.wicket.request.target.coding.MixedParamHybridUrlCodingStrategy;
import org.jboss.jawabot.mod.web.IPageMount;
import org.jboss.jawabot.mod.web.MountProxy;
import org.jboss.jawabot.plugin.reserv.web._pg.LeavePage;
import org.jboss.jawabot.plugin.reserv.web._pg.ResourcePage;
import org.jboss.jawabot.plugin.reserv.web._pg.TakePage;

/**
 *  Mounts Logger pages into Wicket.
 *  @author Ondrej Zizka
 */
public class ReservPageMount implements IPageMount {

    @Override
    public void mount( MountProxy wicketApp ) {
        /*wicketApp.mount( new NonVersionedMixedParamHybridUrlCodingStrategy(
                "reserv", ReservationsPage.class, new String[]{"resource"}
        ));*/
        
        //wicketApp.mountBookmarkablePage("res",  ResourcesPage.class);
        //wicketApp.mountBookmarkablePage("res/",  ResourcePage.class);
        wicketApp.mount(new MixedParamHybridUrlCodingStrategy("res", ResourcePage.class, new String[]{"name"}));
        
        wicketApp.mountBookmarkablePage("take",  TakePage.class);
        wicketApp.mountBookmarkablePage("leave", LeavePage.class);
        
    }
    
}// class

