
package org.jboss.jawabot.plugin.reserv.web._co;

import cz.dynawest.util.DateUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.plugin.reserv.bus.ReservationWrap;
import org.jboss.jawabot.plugin.reserv.bus.Resource;
import org.jboss.jawabot.plugin.reserv.bus.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  
 *  @author Ondrej Zizka
 */
public class ResourceDetailPanel extends Panel {
   private static final Logger log = LoggerFactory.getLogger( ResourceDetailPanel.class );
   
   
   @Inject private ResourceManager resourceManager;
   

   public ResourceDetailPanel( String id, final String name ) {
      super( id );
      this.setDefaultModel( new Model( resourceManager.getResource( name ) ) );
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
      List<ReservationWrap> resvs = resourceManager.getReservationsForResource( this.getResource() );

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
