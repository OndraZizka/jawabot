
package org.jboss.jawabot.plugin.pastebin.web._co;

import cz.dynawest.util.DateUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.jboss.jawabot.plugin.pastebin.ent.PasteBinEntry;
import org.jboss.jawabot.plugin.pastebin.web._pg.PasteBinShowPage;

/**
 *  
 *  @author Ondrej Zizka
 */
public class PastebinLinkPanel extends Panel {
   
   
   /*public PastebinLinkPanel( String id, final int pasteId ) {
      
   }*/

   public PastebinLinkPanel( String id, final PasteBinEntry pbe ) {
      super( id, new Model<PasteBinEntry>(pbe) );
   }

   @Override
   protected void onInitialize() {
      super.onInitialize();
      BookmarkablePageLink<PasteBinEntry> link = new BookmarkablePageLink<PasteBinEntry>( "link", PasteBinShowPage.class);
      link.add( new Image( "icoType", "PasteBinEntry.gif" ) );
      link.add( new Label("label", "" + constructLabel( ((PasteBinEntry)getDefaultModelObject()) )));
      add(link);
   }

   private String constructLabel( PasteBinEntry pasteBinEntry ) {
      return pasteBinEntry.getAuthor() + " " + DateUtils.createRelativeTimeString( pasteBinEntry.getWhen() );
   }
   
   
   
}
