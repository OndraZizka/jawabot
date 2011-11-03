
package org.jboss.jawabot.plugin.logger.web._co;

import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.datetime.DateConverter;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.jboss.jawabot.plugin.logger.bus.ChannelLogManager;
import org.jboss.jawabot.plugin.logger.ent.ChannelLogInfo;

/**
 *  Panel with a table showing logs.
 *  @author Ondrej Zizka
 * 
 *  TODO: The way I instantiate LDM smells. Perhaps I should avoid 
 *        making it a CDI bean and set it's EntityManager manually?
 */
public class LoggedChannelsListPanel extends Panel {
   
   @Inject private ChannelLogManager channelLogManager;
   
   public LoggedChannelsListPanel( String id ) {
      super( id );
   }

   
   
   @Override
   protected void onInitialize() {
        super.onInitialize();
        
        final LoadableDetachableModel<List<ChannelLogInfo>> ldm =
        new LoadableDetachableModel<List<ChannelLogInfo>>() {
            protected List<ChannelLogInfo> load() {
                return channelLogManager.getLoggedChannelInfos();
            }
        };
        
        final DateConverter converter = new PatternDateConverter("yyyy-MM-dd", true);

        add(new ListView<ChannelLogInfo>("channels", ldm ) {
           @Override protected void populateItem(ListItem<ChannelLogInfo> item) {
                //new CompoundPropertyModel<ChannelLogInfo>( item.getModelObject() );
                ChannelLogInfo info = item.getModelObject();
                item.add( new ChannelLogLinkSimplePanel("link", info.getName() ));
                item.add(new Label("count", "" + info.getCount() ));
                item.add(new DateLabel("first", new Model( info.getFirst() ), converter ));
                item.add(new DateLabel("last",  new Model( info.getLast() ), converter ));
           }
        });

   }
   
}
