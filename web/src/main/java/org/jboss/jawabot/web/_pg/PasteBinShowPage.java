
package org.jboss.jawabot.web._pg;

import cz.dynawest.util.DateUtils;
import cz.dynawest.wicket.WMC;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.PropertyModel;
import org.jboss.jawabot.plugin.irc.web._co.ChannelLinkSimplePanel;
import org.jboss.jawabot.plugin.pastebin.PasteBinEntry;
import org.jboss.jawabot.web._base.BaseLayoutPage;
import org.jboss.jawabot.web._co.UserLinkSimplePanel;


/** 
 *  
 *  @author Ondrej Zizka
 */
public class PasteBinShowPage extends BaseLayoutPage {

   public PasteBinShowPage( PasteBinEntry entry ) {
      super( PageParameters.NULL );

      add( new UserLinkSimplePanel( "author", new PropertyModel( entry, "author" ) ) );
      add( new WMC( "hasFor" ).add(
              new UserLinkSimplePanel( "for", new PropertyModel( entry, "for" ) ) 
           ).setVisible( entry.hasAuthor() ) );
      add( new Label( "when", DateUtils.toStringSQL( entry.getWhen() ) ) );
      add( new WMC( "hasChannel" ).add(
              new ChannelLinkSimplePanel( "channel", entry.getChannel() )
           ).setVisible( entry.hasChannel() ) );
      add( new MultiLineLabel( "text", new PropertyModel( entry, "text" ) ) );

   }

}// class
