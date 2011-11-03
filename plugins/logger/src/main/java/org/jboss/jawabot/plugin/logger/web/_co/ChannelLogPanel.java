
package org.jboss.jawabot.plugin.logger.web._co;

import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.jboss.jawabot.irc.ent.IrcEvent;
import org.jboss.jawabot.plugin.irc.Channel;
import org.jboss.jawabot.plugin.irc.ChannelManager;

/**
 *  
 *  @author Ondrej Zizka
 */
public class ChannelLogPanel extends Panel {
   
   @Inject private ChannelManager channelManager;
   
   public ChannelLogPanel( String id ) {
      super( id );
      super.setDefaultModel( new ListModel( channelManager.getChannelsRange_OrderByWhenDesc(0, 2000) ) ); // TBD
   }

   public ChannelLogPanel( String id, final Channel ch ) {
      super( id, new Model<Channel>(ch) );
   }

   
   
   @Override
   protected void onInitialize() {
      super.onInitialize();

        add(new ListView<IrcEvent>("messages", (ListModel) this.getDefaultModel() ) {
           @Override protected void populateItem(ListItem<IrcEvent> item) {
              item.add(new Label("nick", item.getModelObject().getUser() ));
              item.add(new Label("msg", item.getModelObject().getText() ));
           }
        });
        
   }
   
   
   
}
