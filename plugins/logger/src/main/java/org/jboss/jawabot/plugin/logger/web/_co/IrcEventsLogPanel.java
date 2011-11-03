
package org.jboss.jawabot.plugin.logger.web._co;

import javax.inject.Inject;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.jboss.jawabot.irc.ent.IrcEvent;
import org.jboss.jawabot.plugin.logger.irc.IrcEventCriteria;
import org.jboss.jawabot.plugin.logger.web.IrcEventCriteriaLDM;


/**
 *  
 *  @author Ondrej Zizka
 */
public class IrcEventsLogPanel extends Panel {
    
    /*public IrcEventsLogPanel( String id, final List<IrcEvent> events ) {
        super( id, new ListModel<IrcEvent>(events) );
    }*/
    
    @Inject private IrcEventCriteriaLDM model;

    public IrcEventsLogPanel( String id, final IrcEventCriteria crit ) {
        super( id );
        model.setCrit(crit);
        this.setDefaultModel(model);
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
