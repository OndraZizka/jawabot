
package org.jboss.jawabot.web._co;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.jboss.jawabot.Resource;
import org.jboss.jawabot.web._pg.ResourcePage;

/**
 *  
 *  @author ondra
 */
public class ResourceLinkPanel extends Panel {
   
   Resource res;
   
   public ResourceLinkPanel( String id, final String name ) {
      
   }

   public ResourceLinkPanel( String id, final Resource res ) {
      super( id, new Model<Resource>(res) );
   }

   @Override
   protected void onInitialize() {
      super.onInitialize();
      add( new BookmarkablePageLink<Resource>( "link", ResourcePage.class){{
         add( new Image( "icoType", "ResourceMachine.gif" ) );
         add( new Label("label", res.getName() ));
      }});
   }
   
   
   
}
