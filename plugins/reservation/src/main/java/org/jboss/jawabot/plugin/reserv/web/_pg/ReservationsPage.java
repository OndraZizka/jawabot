package org.jboss.jawabot.plugin.reserv.web._pg;


import cz.dynawest.util.DateUtils;
import cz.dynawest.wicket.BookmarkablePageLink;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import org.slf4j.Logger; import org.slf4j.LoggerFactory; 
import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.string.Strings;
import org.jboss.jawabot.plugin.reserv.bus.Reservation;
import org.jboss.jawabot.plugin.reserv.bus.Resource;
import org.jboss.jawabot.usermgr.UserManager;
import org.jboss.jawabot.web._base.BaseLayoutPage;
import org.jboss.jawabot.plugin.reserv.bus.ReservationWrap;
import org.jboss.jawabot.plugin.reserv.bus.ResourceManager;
import org.jboss.jawabot.plugin.reserv.bus.ResourceWithNearestFreePeriodDTO;
import org.jboss.jawabot.plugin.reserv.web._co.ReservationListPanel;
import org.jboss.jawabot.plugin.reserv.web._co.ReserveLinkPanel;
import org.jboss.jawabot.plugin.reserv.web._co.ResourceLinkPanel;


/**
 *  List of logged channels. 
 * 
 *  @author Ondrej Zizka
 */
public class ReservationsPage extends BaseLayoutPage {
    private static final Logger log = LoggerFactory.getLogger(ReservationsPage.class);

    
    @Inject ResourceManager resourceManager;
    
    @Inject UserManager userManager;
    
    
    public ReservationsPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        
        final boolean logged = true; // TODO
        
        
         // -- Resources table with "reserve" links. --
         // We are keeping it here as non-component since it's only at home page.
         // Todo: Make a component anyway.

         List<ResourceWithNearestFreePeriodDTO> resources = getResourceWithNearestFreePeriod( resourceManager.getResources_SortByName() );

         add(new ListView<ResourceWithNearestFreePeriodDTO>("resources", new ListModel<ResourceWithNearestFreePeriodDTO>( resources ) ) {
            @Override protected void populateItem(ListItem<ResourceWithNearestFreePeriodDTO> item) 
            {
               ResourceWithNearestFreePeriodDTO obj = item.getModelObject();

               item.add(
                   new ResourceLinkPanel("reslink", item.getModelObject().getResource() ) 
               );
               String[] ids    = new String[]{"today", "todayPlus1", "todayPlus2", "todayPlus3"};
               String[] labels = new String[]{"today", "tomorrow", "+2", "+3"};
               for( int i = 0; i < ids.length; i++ ) {
                  item.add(
                      new BookmarkablePageLink( ids[i], ReservePage.class )
                      .setParameter( ReservePage.PARAM_FROM_OFFSET, 0)
                      .setParameter( ReservePage.PARAM_TO_OFFSET, i)
                      .setParameter( ReservePage.PARAM_RES, obj.getResource().getName() )
                      .setParameter( ReservePage.PARAM_USER, ReservationsPage.this.getSession().getLoggedUser() )
                      .add( new Label("label", labels[i] ))
                      .setEnabled( logged && item.getModelObject().isFreeTodayPlusX( i ) )
                  );
               }
               item.add(
                   new ReserveLinkPanel("chooseTimeSpan", new org.jboss.jawabot.plugin.reserv.state.ent.Reservation( obj.getResource(), 0, 4 ) )
                   .setEnabled( logged && obj.isFreeToday() )
               );
            }
         });

         add( new TextField( "note", new CompoundPropertyModel(this) ) );


         // Reservations.
         List<ReservationWrap> resvs = resourceManager.getReservations();

         add( new ReservationListPanel("reservations", resvs) );
         /*
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
          */        

    }// onInitialize()
    
    
    
    
    
    
   /**
    * @returns resource wrapped with information about the nearest reservation.
    */
   private List<ResourceWithNearestFreePeriodDTO> getResourceWithNearestFreePeriod( List<Resource> resources ) {
		List<ResourceWithNearestFreePeriodDTO> resDTOs = new ArrayList();
		for( Resource res : resources )
      {
         final Date NOW = new Date();

         Reservation nearestResv = resourceManager.getNearestFutureReservationForResource(res);
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
            
            return userManager.getUsersNameStartsWith( input.toLowerCase(), 0, 80 ).iterator() ;
         }
      };
      return field;
   }

   
   
    
}// class LoggerChannelsListPage
