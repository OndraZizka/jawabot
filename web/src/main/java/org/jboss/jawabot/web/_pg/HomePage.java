
package org.jboss.jawabot.web._pg;


import cz.dynawest.util.DateUtils;
import cz.dynawest.wicket.BookmarkablePageLink;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.*;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.string.Strings;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.Reservation;
import org.jboss.jawabot.ReservationWrap;
import org.jboss.jawabot.Resource;
import org.jboss.jawabot.resmgr.ResourceWithNearestFreePeriodDTO;
import org.jboss.jawabot.state.ent.User;
import org.jboss.jawabot.web._co.ResourceLinkPanel;



/**
 *
 * @author Ondrej Zizka
 */
public class HomePage extends BaseLayoutPage
{
  private static final Logger log = Logger.getLogger( HomePage.class.getName() );


  private String note = "";



  public HomePage( PageParameters parameters ) {
     super(parameters);

     // Resources.
     List<ResourceWithNearestFreePeriodDTO> resources = getResourceWithNearestFreePeriod( JawaBotApp.getJawaBot().getResourceManager().getResources_SortByName() );

     add(new ListView<ResourceWithNearestFreePeriodDTO>("resources", new ListModel<ResourceWithNearestFreePeriodDTO>( resources ) ) {
        @Override protected void populateItem(ListItem<ResourceWithNearestFreePeriodDTO> item) {
           item.add(
               new ResourceLinkPanel("reslink", item.getModelObject().getResource() ) 
           );
           String[] ids    = new String[]{"today", "todayPlus1", "todayPlus2", "todayPlus3"};
           String[] labels = new String[]{"today", "tomorrow", "+2", "+3"};
           for( int i = 0; i < ids.length; i++ ) {
              item.add(
                  new BookmarkablePageLink( ids[i], ReservePage.class ){
                     
                  }
                  .setParameter("fromDateOffset", 0)
                  .setParameter("toDateOffset", i)
                  .add( new Label("label", labels[i] ))
                  .setEnabled( item.getModelObject().isFreeTodayPlusX( i ) )
              );
           }
           item.add(
               new BookmarkablePageLink("chooseTimeSpan", ReservePage.class){}
               .setParameter("fromDateOffset", 0)
               .setParameter("toDateOffset", 4)
               .add( new Label("label", "choose span..." ))
               .setEnabled( item.getModelObject().isFreeToday() )
           );
        }
     });

     add( new TextField( "note", new CompoundPropertyModel(this) ) );


     // Reservations.
     List<ReservationWrap> resvs = JawaBotApp.getJawaBot().getResourceManager().getReservations();

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





   /**
    * @returns resource wrapped with information about the nearest reservation.
    */
   private List<ResourceWithNearestFreePeriodDTO> getResourceWithNearestFreePeriod( List<Resource> resources ) {
		List<ResourceWithNearestFreePeriodDTO> resDTOs = new ArrayList();
		for( Resource res : resources )
      {
         final Date NOW = new Date();

         Reservation nearestResv = JawaBotApp.getJawaBot().getResourceManager().getNearestFutureReservationForResource(res);
         ResourceWithNearestFreePeriodDTO resDTO;
         if( nearestResv == null )
            resDTO = new ResourceWithNearestFreePeriodDTO(res, 0, -1 );
         else if( nearestResv.getFrom().before( NOW ) )
            resDTO = new ResourceWithNearestFreePeriodDTO(res, -1, DateUtils.getDaysDiff( NOW, nearestResv.getFrom() ));
         else
            resDTO = new ResourceWithNearestFreePeriodDTO(res,
                    DateUtils.getDaysDiff( NOW, nearestResv.getFrom() ),
                    DateUtils.getDaysDiff( NOW, nearestResv.getTo() )
            );
         resDTOs.add(resDTO);
		}
		return resDTOs;
   }



   /**
    *  Prepared for user field auto-completion.
    */
   public AutoCompleteTextField getAutoCompleteTextField() {
      final AutoCompleteTextField field = new AutoCompleteTextField( "user", new Model("") ) {

         @Override
         protected Iterator getChoices( String input ) {
            if ( Strings.isEmpty( input ) ) {
               return Collections.EMPTY_LIST.iterator();
            }

            input = input.toLowerCase();

            List<User> users = JawaBotApp.getUserManager().getUsers_OrderByName();
            for( User user : users )
            {
               String name = user.getName().toLowerCase();
               if( StringUtils.isBlank( name ) ) continue;
               if( name.startsWith( input )
                || name.substring(1).startsWith( input ) 
               ) {
                  users.add( user );
                  if ( users.size() == 20 ) break;
               }
            }
            return users.iterator();
         }
      };
      return field;
   }




}// class HomePage
