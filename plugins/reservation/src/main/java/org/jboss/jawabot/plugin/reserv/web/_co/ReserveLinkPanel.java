
package org.jboss.jawabot.plugin.reserv.web._co;

import javax.inject.Inject;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import org.jboss.jawabot.plugin.reserv.bus.ResourceManager;
import org.jboss.jawabot.plugin.reserv.state.ent.Reservation;
import org.jboss.jawabot.plugin.reserv.web._pg.ReservePage;

/**
 *  
 *  @author Ondrej Zizka
 */
public class ReserveLinkPanel extends Panel {
   
   @Inject private ResourceManager resourceManager;
   

   public ReserveLinkPanel( String id, final String name ) {
      super( id );
      this.setDefaultModel( new Model( resourceManager.getResource( name ) ) );
   }

   public ReserveLinkPanel( String id, final Reservation resv ) {
      super( id, new Model<Reservation>(resv) );
      //IBehavior a = new CssModifier( , null)
   }

   @Override
   protected void onInitialize() {
      super.onInitialize();
      this.setRenderBodyOnly( true );
      
      Reservation resv = (Reservation) getDefaultModelObject();
      
      BookmarkablePageLink<Reservation> link = new BookmarkablePageLink<Reservation>( "link", ReservePage.class);
      // Is this correct? Can't it be mapped from an object?
      link.setParameter( ReservePage.PARAM_RES, resv.getRes().getName() );
      link.setParameter( ReservePage.PARAM_FROM_OFFSET, resv.getRes().getName() );
      link.setParameter( ReservePage.PARAM_TO_OFFSET, resv.getRes().getName() );
      link.add( new Image( "icoType", new ResourceReference(this.getClass(), "Calendar.gif") ) );
      link.add( new Label("label", ((Reservation)getDefaultModelObject()).getRes().getName() ));
      add(link);
   }
 
   
   
}
