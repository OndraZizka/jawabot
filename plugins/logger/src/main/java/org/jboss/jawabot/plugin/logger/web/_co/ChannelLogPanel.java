
package org.jboss.jawabot.plugin.logger.web._co;

import java.util.Date;
import javax.inject.Inject;
import org.apache.commons.lang.time.DateUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.util.ListModel;
import org.jboss.jawabot.irc.ent.IrcEvent;
import org.jboss.jawabot.plugin.irc.Channel;
import org.jboss.jawabot.plugin.logger.bus.ChannelLogManager;
import org.jboss.jawabot.plugin.logger.ent.IrcEventsCriteria;
import org.jboss.jawabot.plugin.logger.web.IrcEventCriteriaLDM;

/**
 *  Panel with a table showing logs.
 *  @author Ondrej Zizka
 * 
 *  TODO: The way I instantiate LDM smells. Perhaps I should avoid 
 *        making it a CDI bean and set it's EntityManager manually?
 */
public class ChannelLogPanel extends Panel {
   
   @Inject private ChannelLogManager channelLogManager;
   
   @Inject IrcEventCriteriaLDM model;

   
   public ChannelLogPanel( String id, Channel ch ) {
      super( id );
      IrcEventsCriteria crit = new IrcEventsCriteria(ch.getName(), DateUtils.addHours(new Date(), -1), new Date());
      this.model.setCrit( crit );
      super.setDefaultModel( this.model );
   }

   
   
   @Override
   protected void onInitialize() {
        super.onInitialize();
        
        IrcEventCriteriaLDM ldm = (IrcEventCriteriaLDM) this.getDefaultModel();
        ListModel lm = new ListModel( ldm.getObject() );

        add(new ListView<IrcEvent>("messages", lm ) {
           @Override protected void populateItem(ListItem<IrcEvent> item) {
              item.add(new Label("nick", item.getModelObject().getUser() ));
              item.add(new Label("msg", item.getModelObject().getText() ));
           }
        });

   }
   
}
