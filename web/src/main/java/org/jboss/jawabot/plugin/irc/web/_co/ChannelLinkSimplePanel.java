
package org.jboss.jawabot.plugin.irc.web._co;

import org.slf4j.Logger; import org.slf4j.LoggerFactory; 
import cz.dynawest.wicket.PageParametersUtil;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.jboss.jawabot.plugin.irc.Channel;
import org.jboss.jawabot.web._pg.ChannelPage;

/**
 *  
 *  @author Ondrej Zizka
 */
public class ChannelLinkSimplePanel extends Panel {
   private static final Logger log = LoggerFactory.getLogger( ChannelLinkSimplePanel.class );
   
   public ChannelLinkSimplePanel( String id, final String name ) {
      super( id, new Model(name) );

      BookmarkablePageLink<Channel> link = new BookmarkablePageLink<Channel>( "link", ChannelPage.class, PageParametersUtil.create("name", name));
      link.add( new Image( "icoType", "Channel.gif" ) );
      link.add( new Label("label", name ));
      add(link.setVisible( null != name ));
      
   }
   
}
