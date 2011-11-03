
package org.jboss.jawabot.plugin.reserv.web._co;

import javax.inject.Inject;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.util.ListModel;
import org.jboss.jawabot.plugin.reserv.bus.Resource;
import org.jboss.jawabot.plugin.reserv.bus.ResourceManager;

/**
 *  
 *  @author Ondrej Zizka
 */
public class ResourcesListPanel extends Panel {
    
    @Inject private ResourceManager resourceManager;
   
   
    public ResourcesListPanel( String id ) {
        super( id );
        this.setDefaultModel( new ListModel( resourceManager.getResources_SortByName() ) );
    }


   
    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new ListView<Resource>("res", (ListModel) this.getDefaultModel() ) {
            @Override protected void populateItem(ListItem<Resource> item) {
                item.add(new ResourceLinkPanel("link", item.getModelObject() ));
            }
        });
        
   }
    
}
