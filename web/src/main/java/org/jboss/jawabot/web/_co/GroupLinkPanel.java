
package org.jboss.jawabot.web._co;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.Resource;
import org.jboss.jawabot.web._pg.ResourcePage;

/**
 *  
 *  @author ondra
 */
public class GroupLinkPanel extends Panel {
   
   //Resource res;

   public GroupLinkPanel( String id, final String name ) {
      super( id, new Model( JawaBotApp.getJawaBot().getResourceManager().getResource( name ) ) );
      //Resource res = JawaBotApp.getJawaBot().getResourceManager().getResource( name );
      //this.res = res;
   }

   public GroupLinkPanel( String id, final Resource res ) {
      super( id, new Model<Resource>(res) );
      //IBehavior a = new CssModifier( , null)
   }

   @Override
   protected void onInitialize() {
      super.onInitialize();
      BookmarkablePageLink<Resource> link = new BookmarkablePageLink<Resource>( "link", ResourcePage.class);
      link.add( new Image( "icoType", "Group.gif" ) );
      link.add( new Label("label", ((Resource)getDefaultModelObject()).getName() ));
      add(link);
   }
 
   
   
}
