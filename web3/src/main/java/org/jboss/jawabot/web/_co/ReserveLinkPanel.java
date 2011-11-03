
package org.jboss.jawabot.web._co;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.state.ent.Reservation;
import org.jboss.jawabot.web._pg.ReservePage;

/**
 *  
 *  @author Ondrej Zizka
 */
public class ReserveLinkPanel extends Panel {
   
   //Resource res;

   public ReserveLinkPanel( String id, final String name ) {
      super( id, new Model( JawaBotApp.getJawaBot().getResourceManager().getResource( name ) ) );
      //Resource res = JawaBotApp.getJawaBot().getResourceManager().getResource( name );
      //this.res = res;
   }

   public ReserveLinkPanel( String id, final Reservation resv ) {
      super( id, new Model<Reservation>(resv) );
      //IBehavior a = new CssModifier( , null)
   }

   @Override
   protected void onInitialize() {
      super.onInitialize();
      this.setRenderBodyOnly( true );
      
      Reservation resv = (Reservation)getDefaultModelObject();
      
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
