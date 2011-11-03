package org.jboss.jawabot.plugin.logger.web._menu;

import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.jboss.jawabot.plugin.logger.bus.ChannelLogManager;
import org.jboss.jawabot.plugin.logger.ent.ChannelLogInfo;
import org.jboss.jawabot.plugin.logger.web._co.ChannelLogLinkSimplePanel;

/**
 *  
 *  @author Ondrej Zizka
 */
public class LoggerMenuPanel extends Panel {
    
    @Inject ChannelLogManager channelLogManager;

    
    public LoggerMenuPanel(String id) {
        super(id);
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
        
        add(new ListView<ChannelLogInfo>("channels", ldm ) {
           @Override protected void populateItem(ListItem<ChannelLogInfo> item) {
                //new CompoundPropertyModel<ChannelLogInfo>( item.getModelObject() );
                ChannelLogInfo info = item.getModelObject();
                item.add( new ChannelLogLinkSimplePanel("link", info.getName() ));
                boolean newsForCurrentUser = false; // TBD
                item.add(new Label("newFlag").setVisible( newsForCurrentUser ));
           }
        });
        
   }// onInitialize()
    
}// class

