
package org.jboss.jawabot.web._pg;

import cz.dynawest.util.DateUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.PropertyModel;
import org.jboss.jawabot.plugin.pastebin.PasteBinEntry;
import org.jboss.jawabot.web._base.BaseLayoutPage;


/** 
 *  
 *  @author Ondrej Zizka
 */
public class PasteBinShowPage extends BaseLayoutPage {

   public PasteBinShowPage( PasteBinEntry entry ) {
      super( PageParameters.NULL );

      add( new Label( "by", new PropertyModel( entry, "by" ) ) );
      add( new Label( "for", new PropertyModel( entry, "for" ) ) );
      add( new Label( "when", DateUtils.toStringSQL( entry.getWhen() ) ) );
      //add( new Label( "channel", new PropertyModel( entry, "channel" ) ) );
      add( new MultiLineLabel( "text", new PropertyModel( entry, "text" ) ) );

   }

}// class
