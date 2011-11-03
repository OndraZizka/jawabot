
package org.jboss.jawabot.plugin.irc.web._co;

import javax.inject.Inject;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.jboss.jawabot.plugin.irc.Channel;
import org.jboss.jawabot.plugin.irc.ChannelManager;

/**
 *  
 *  @author Ondrej Zizka
 */
public class ChannelListPanel extends Panel {
   
   @Inject private ChannelManager channelManager;
   
   public ChannelListPanel( String id ) {
      super( id );
      super.setDefaultModel( new ListModel( channelManager.getChannelsRange_OrderByWhenDesc(0, 2000) ) ); // TBD
   }

   public ChannelListPanel( String id, final Channel ch ) {
      super( id, new Model<Channel>(ch) );
   }

   
   
   @Override
   protected void onInitialize() {
      super.onInitialize();

        add(new ListView<Channel>("channels", (ListModel) this.getDefaultModel() ) {
           @Override protected void populateItem(ListItem<Channel> item) {
              item.add(new ChannelLinkPanel("link", item.getModelObject() ));
           }
        });
        
   }
   
   
   
}
