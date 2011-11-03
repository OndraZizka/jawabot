package org.jboss.jawabot.plugin.reserv.web;

import javax.enterprise.context.Dependent;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.jboss.jawabot.mod.web.ITabBarContrib;
import org.jboss.jawabot.plugin.reserv.web._pg.ReservationsPage;

/**
 *  
 *  @author Ondrej Zizka
 */
@Dependent
public class ReservTabContrib implements ITabBarContrib {

    @Override
    public Label getLabel( String id ) {
        return new Label( id, "Reservations");
    }

    @Override
    public Class<? extends WebPage> getLinkedPage() {
        return ReservationsPage.class;
    }
    
}// class

