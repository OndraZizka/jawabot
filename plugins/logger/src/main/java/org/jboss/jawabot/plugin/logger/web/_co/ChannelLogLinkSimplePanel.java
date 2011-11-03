
package org.jboss.jawabot.plugin.logger.web._co;

import cz.dynawest.wicket.PageParametersUtil;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.jawabot.plugin.logger.web._pg.ChannelLogPage;

/**
 *  Uses only string.
 *  @author Ondrej Zizka
 */
public class ChannelLogLinkSimplePanel extends Panel {
   
   public ChannelLogLinkSimplePanel( String id, final String name ) {
      this( id, new Model( escapeHashes(name) ) );
   }

   public ChannelLogLinkSimplePanel( String id, final IModel<String> nameModel ) {
      super( id, nameModel );
      
      String name = nameModel.getObject();
      BookmarkablePageLink<String> link = new BookmarkablePageLink<String>( "link", ChannelLogPage.class, PageParametersUtil.create("name", name));
      link.add( new Image( "icoType", "Channel.gif" ) );
      link.add( new Label("label", "" + name ));
      add(link.setVisible( null != name ));
      
   }
   
   // https://bugzilla.mozilla.org/show_bug.cgi?id=677038
   private static String escapeHashes( String name ){
       if( name == null ) return null;
       return name.replaceAll("#", "=");
   }

   // https://bugzilla.mozilla.org/show_bug.cgi?id=677038
   public static String unescapeHashes( String name ){
       if( name == null ) return null;
       return name.replaceAll("=", "#");
   }

}
