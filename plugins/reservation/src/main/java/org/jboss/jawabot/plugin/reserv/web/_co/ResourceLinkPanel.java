
package org.jboss.jawabot.plugin.reserv.web._co;

import cz.dynawest.wicket.PageParametersUtil;
import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.plugin.reserv.bus.Resource;
import org.jboss.jawabot.plugin.reserv.bus.ResourceManager;
import org.jboss.jawabot.plugin.reserv.web._pg.ResourcePage;


/**
 *  
 *  @author Ondrej Zizka
 */
public class ResourceLinkPanel extends Panel {
   
   @Inject private ResourceManager resourceManager;
   private String nameToLookupLater;
   

   public ResourceLinkPanel( String id, final String name )
   {
      super( id );
      this.nameToLookupLater = name;
   }

   public ResourceLinkPanel( String id, final Resource res ) {
      super( id, new Model<Resource>(res) );
   }

   @Override
   protected void onInitialize() {
      this.setDefaultModel( new Model( resourceManager.getResource( this.nameToLookupLater ) ) );

      super.onInitialize();
      
      BookmarkablePageLink<Resource> link = new BookmarkablePageLink<Resource>( "link", ResourcePage.class, 
              PageParametersUtil.create( ResourcePage.PARAM_NAME, this.getDefaultModelObjectAsString())
      );
      link.add( new Image( "icoType", "ResourceMachine.gif" ) );
      link.add( new Label("label", ((Resource)getDefaultModelObject()).getName() ));
      add(link);
   }
   
   
   
}
