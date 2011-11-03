
package org.jboss.jawabot.web._pg;


import cz.dynawest.util.DateUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.util.ListModel;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.ReservationWrap;
import org.jboss.jawabot.Resource;


/**
 *
 * @author Ondrej Zizka
 */
public class ResourcePage extends BaseLayoutPage
{
  private static final Logger log = Logger.getLogger( HomePage.class.getName() );

  
  /** Page const */
  public ResourcePage( PageParameters params ) {
     super(params);
     
     log.debug(" Page params: " + params.toString() );
     
     // Resource
     //Resource resource = new Resource("jawa05", "FooGroup");
     Resource resource = JawaBotApp.getJawaBot().getResourceManager().getResource( params.getString("name") );

     add( new Label("res", resource.getName()) );
     

     // Resource's groups
     List<String> groups = new ArrayList();
     groups.add( resource.getProject() );
     groups.add( "FooGroup" );
     groups.add( "BarGroup" );

     add(new ListView<String>("groups", new ListModel( groups ) ) {
        @Override protected void populateItem(ListItem<String> item) {
           item.add(new Label("group", item.getModelObject() ));
           item.setVisible( StringUtils.isBlank( item.getModelObject() ) );
        }
     });

     

     // Reservations.
     List<ReservationWrap> resvs = JawaBotApp.getJawaBot().getResourceManager().getReservationsForResource(resource);

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


}// class HomePage
