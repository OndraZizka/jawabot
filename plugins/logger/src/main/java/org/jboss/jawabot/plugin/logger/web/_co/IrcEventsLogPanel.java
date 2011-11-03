
package org.jboss.jawabot.plugin.logger.web._co;

import java.util.Date;
import java.util.List;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.jboss.jawabot.irc.ent.IrcEvent;

/**
 *  
 *  @author Ondrej Zizka
 */
public class IrcEventsLogPanel extends Panel {
   
   public IrcEventsLogPanel( String id, final List<IrcEvent> events ) {
      super( id, new ListModel<IrcEvent>(events) );
   }

   
   
   @Override
   protected void onInitialize() {
      super.onInitialize();

        add(new ListView<IrcEvent>("events", (ListModel) this.getDefaultModel() ) 
        {
           @Override protected void populateItem( final ListItem<IrcEvent> item ) {
              // Visible content
              item.add(new Label("nick", item.getModelObject().getUser() ));
              item.add(new Label("text", item.getModelObject().getText() ));
              
              // class="..." - by type of the event.
              item.add(new AttributeModifier("class", new Model<String>() {
                  public String getObject() {
                     // IrcEvMessage, IrcEvJoin, IrcEvPart, IrcEvAction, ...
                     return ((IrcEvent)item.getDefaultModelObject()).getClass().getSimpleName();
                  }
              } ));
           }
        });
        
   }// onInitialize()
   
   
}
