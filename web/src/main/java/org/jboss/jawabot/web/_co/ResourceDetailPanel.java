
package org.jboss.jawabot.web._co;

import cz.dynawest.util.DateUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.ReservationWrap;
import org.jboss.jawabot.Resource;

/**
 *  
 *  @author ondra
 */
public class ResourceDetailPanel extends Panel {
   
   //Resource res;

   public ResourceDetailPanel( String id, final String name ) {
      super( id, new Model( JawaBotApp.getJawaBot().getResourceManager().getResource( name ) ) );
      //Resource res = JawaBotApp.getJawaBot().getResourceManager().getResource( name );
      //this.res = res;
   }

   public ResourceDetailPanel( String id, final Resource res ) {
      super( id, new Model<Resource>(res) );
      //IBehavior a = new CssModifier( , null)
   }

   
   private Resource getResource(){
      return (Resource) getDefaultModelObject();
   }

   
   @Override
   protected void onInitialize() {
      super.onInitialize();

        //add( new Label("res", this.getResource().getName()) );


        // Resource's groups - TODO
        List<String> groups = new ArrayList();
        groups.add( this.getResource().getProject() );
        groups.add( "FooGroup" );
        groups.add( "BarGroup" );

        add(new ListView<String>("groups", new ListModel( groups ) ) {
           @Override protected void populateItem(ListItem<String> item) {
              item.add(new Label("group", item.getModelObject() ));
              item.setVisible( StringUtils.isBlank( item.getModelObject() ) );
           }
        });



        // Reservations.
        List<ReservationWrap> resvs = JawaBotApp.getJawaBot().getResourceManager().getReservationsForResource( this.getResource() );

        add(new ListView<ReservationWrap>("reservations", new ListModel<ReservationWrap>( resvs ) ) {
           @Override protected void populateItem(ListItem<ReservationWrap> item) {
              ReservationWrap res = item.getModelObject();
              item.add(new Label("res", res.getResourceName() ));
              item.add(new Label("owner", res.getForUser() ));
              item.add(new Label("from", DateUtils.toStringSQL( res.getFrom() ) ));
              item.add(new Label("to",   DateUtils.toStringSQL( res.getTo()   ) ));
              item.add(new Label("note", "" ));
           }
        });
      
   }
   
   
   
}
