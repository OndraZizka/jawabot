
package org.jboss.jawabot.web._co;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.Resource;

/**
 *  
 *  @author Ondrej Zizka
 */
public class ResourcesListPanel extends Panel {
   
   public ResourcesListPanel( String id ) {
      super( id, new ListModel( JawaBotApp.getJawaBot().getResourceManager().getResources_SortByName() ) );
   }

   public ResourcesListPanel( String id, final Resource res ) {
      super( id, new Model<Resource>(res) );
      //IBehavior a = new CssModifier( , null)
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
