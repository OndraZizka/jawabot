
package org.jboss.jawabot.web._co;


import cz.dynawest.util.DateUtils;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.ReservationWrap;



/**
 * A table with reservations.
 * 
 * @author Ondrej Zizka
 */
public class ReservationListPanel extends Panel
{
   //private static final Logger log = LoggerFactory.getLogger( ReservationListPanel.class );
   @Inject private static Logger log;

   @Inject String user;
   
   @Inject int randomNumber;

   
   

   public ReservationListPanel( String id, List<ReservationWrap> resvs ) {
      this( id, new ListModel<ReservationWrap>( resvs ) );
   }

   public ReservationListPanel( String id, IModel<List<ReservationWrap>> model ) {
     super(id);
     this.setRenderBodyOnly(true);

     //final String user = ;
     
     add( new Label("user", this.user) );

     List<ReservationWrap> resvs = model.getObject();

     // Reservations.
     if( resvs == null ) 
        resvs = JawaBotApp.getJawaBot().getResourceManager().getReservations();

     add(new ListView<ReservationWrap>("reservations", new ListModel<ReservationWrap>( resvs ) ) {
        @Override protected void populateItem(ListItem<ReservationWrap> item) {
           ReservationWrap res = item.getModelObject();
           item.add(new ResourceLinkPanel("res", res.getResourceName() ));
           item.add(new Label("owner", res.getForUser() ));
           item.add(new Label("from", DateUtils.toStringSQL( res.getFrom() ) ));
           item.add(new Label("to",   DateUtils.toStringSQL( res.getTo()   ) ));
           item.add(new Label("note", "" ));
        }
     });
   }
   
   @Override
   protected void onInitialize() {
      super.onInitialize();
   }
  
  


}// class HomePage



