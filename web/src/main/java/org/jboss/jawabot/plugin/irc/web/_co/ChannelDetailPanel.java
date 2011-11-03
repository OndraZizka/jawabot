
package org.jboss.jawabot.plugin.irc.web._co;

import cz.dynawest.util.DateUtils;
import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.ReservationWrap;
import org.jboss.jawabot.Resource;
import org.jboss.jawabot.plugin.irc.Channel;
import org.jboss.jawabot.plugin.irc.ChannelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  
 *  @author Ondrej Zizka
 */
public class ChannelDetailPanel extends Panel {
   private static final Logger log = LoggerFactory.getLogger( ChannelDetailPanel.class );
   
   @Inject private ChannelManager channelManager;
   
   public ChannelDetailPanel( String id, final String name ) {
      super( id );
      super.setDefaultModel( new Model( channelManager.byName( name ) ) );
   }

   public ChannelDetailPanel( String id, final Channel ch ) {
      super( id, new Model<Channel>(ch) );
   }

   
   private Resource getResource(){
      return (Resource) getDefaultModelObject();
   }

   
   @Override
   protected void onInitialize() {
      super.onInitialize();

      // Users.
      List<Channel> channels = channelManager.getUsersInChannel( (Channel) this.getDefaultModelObject() );

      add(new ListView<Channel>("users", new ListModel<Channel>( channels ) ) {
         @Override protected void populateItem(ListItem<Channel> item) {
            Channel chan = item.getModelObject();
            item.add(new Label("user", chan.getName() ));
         }
      });
      
   }
   
}
