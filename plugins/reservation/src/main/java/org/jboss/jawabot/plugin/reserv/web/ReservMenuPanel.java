
package org.jboss.jawabot.plugin.reserv.web;


import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.util.ListModel;
import org.jboss.jawabot.FromService;
import org.jboss.jawabot.plugin.reserv.bus.Resource;
import org.jboss.jawabot.plugin.reserv.bus.ResourceManager;
import org.jboss.jawabot.web.JawaBotSession;
import org.jboss.jawabot.plugin.reserv.web._co.ResourceLinkPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Ondrej Zizka
 */
public class ReservMenuPanel extends Panel {

    private static final Logger log = LoggerFactory.getLogger( ReservMenuPanel.class );
    
    
    @Inject @FromService private ResourceManager resourceManager;


    public ReservMenuPanel( String id ) {
        super( id );
    }


    @Override
    protected void onInitialize() {
        super.onInitialize();



        // Resources.

        //List<Resource> resources = ((ConveniencePageBase)getPage()).getJawaBot().getResourceManager().getResourcesWithNoReservations();
        List<Resource> resources = this.resourceManager.getResourcesWithNoReservations();

        add( new ListView<Resource>( "resourceList", new ListModel( resources ) ) {
            protected void populateItem( ListItem<Resource> item ) {
                item.add( new ResourceLinkPanel( "link", item.getModelObject() ) );
            }
        } );


    }// onInitialize()


    @Override
    public JawaBotSession getSession() {
        return (JawaBotSession) super.getSession();
    } // TODO: CDI.


    private boolean isUserLogged() {
        return null != getSession().getLoggedUser();
    }


    @Override
    protected void onDetach() {
        this.resourceManager = null;
        super.onDetach();
    }
  
  

}// class MenuPanel
