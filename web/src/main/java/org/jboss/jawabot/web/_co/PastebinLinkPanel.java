
package org.jboss.jawabot.web._co;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.Resource;
import org.jboss.jawabot.plugin.pastebin.PasteBinEntry;
import org.jboss.jawabot.web._pg.ResourcePage;

/**
 *  
 *  @author Ondrej Zizka
 */
public class PastebinLinkPanel extends Panel {
   
   //Resource res;

   public PastebinLinkPanel( String id, final int pasteId ) {
      super( id, new Model( JawaBotApp.getJawaBot().getPasteBinManager().getPaste( pasteId ) ) );
   }

   public PastebinLinkPanel( String id, final PasteBinEntry pbe ) {
      super( id, new Model<PasteBinEntry>(pbe) );
   }

   @Override
   protected void onInitialize() {
      super.onInitialize();
      BookmarkablePageLink<Resource> link = new BookmarkablePageLink<Resource>( "link", ResourcePage.class);
      link.add( new Image( "icoType", "PasteBinEntry.gif" ) );
      link.add( new Label("label", "" + ((PasteBinEntry)getDefaultModelObject()).getId() ));
      add(link);
   }
   
   
   
}
