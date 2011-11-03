
package org.jboss.jawabot.plugin.irc.web._co;

import cz.dynawest.wicket.PageParametersUtil;
import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.jboss.jawabot.plugin.irc.Channel;
import org.jboss.jawabot.plugin.irc.ChannelManager;
import org.jboss.jawabot.state.ent.User;
import org.jboss.jawabot.web._pg.ChannelPage;

/**
 *  
 *  @author Ondrej Zizka
 * 
 *  TODO: Remove chanmanager, only operate over entity.
 */
public class ChannelLinkPanel extends Panel {
   
   @Inject private ChannelManager chanManager;

   public ChannelLinkPanel( String id, final long chanID ) {
      super( id );
      super.setDefaultModel( new Model( chanManager.byID(chanID)) );
   }

   public ChannelLinkPanel( String id, final String name ) {
      super( id );
      super.setDefaultModel( new Model( chanManager.byName(name)) );
   }

   public ChannelLinkPanel( String id, final Channel user ) {
      super( id, new Model(user) );
   }

   @Override
   protected void onInitialize() {
      super.onInitialize();
      Channel ch = (Channel)getDefaultModelObject();
      BookmarkablePageLink<Channel> link = new BookmarkablePageLink<Channel>( "link", ChannelPage.class, PageParametersUtil.create("name", ch.getName()));
      link.add( new Image( "icoType", "Channel.gif" ) );
      link.add( new Label("label", "" + ch.getName() ));
      add(link);
   }
   
   
   
}
