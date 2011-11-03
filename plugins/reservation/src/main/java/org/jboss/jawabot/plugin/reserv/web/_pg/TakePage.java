package org.jboss.jawabot.plugin.reserv.web._pg;

import cz.dynawest.util.DateUtils;
import cz.dynawest.wicket.ListColumnGridDataProvider;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.datetime.DateConverter;
import org.jboss.jawabot.ex.UnknownResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.GridView;
import org.apache.wicket.model.PropertyModel;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.plugin.reserv.bus.ReservationWrap;
import org.jboss.jawabot.plugin.reserv.bus.Resource;
import org.jboss.jawabot.plugin.reserv.bus.ResourceManager;
import org.jboss.jawabot.plugin.reserv.bus.ResourceManager.ReservationsBookingResult;
import org.jboss.jawabot.web._base.BaseLayoutPage;
import org.jboss.jawabot.plugin.reserv.web._co.ReservationListPanel;

/**
 *
 * @author Ondrej Zizka
 */
public class TakePage extends BaseLayoutPage {
   private static final Logger log = LoggerFactory.getLogger( TakePage.class );
   
   private final DateConverter DATE_CONV = new StyleDateConverter( "S-", true ); // Thread safe?
   
   
   @Inject private ResourceManager resourceManager;   
   
   
   
   // -- Form backing. --
   private ReservationWrap resv = new ReservationWrap( "ozizka", new Date(), DateUtils.addDays( new Date(), 1 ), null );
   private List<Resource> chosen = new ArrayList();
   private String note;
   
   
   // Action result
   private ReservationsBookingResult reservationResult;
   public ReservationsBookingResult getReservationResults() { return reservationResult; }
      
   
   
   // -- Const --
   public TakePage( PageParameters parameters ) {
      super( parameters );


      // Current user as default.
      this.resv.setForUser( StringUtils.defaultString( this.getSession().getLoggedUser() ) );


      // Free resources.
      List<Resource> resources = resourceManager.getResources_SortByName();


      // Form
      Form form = new Form( "form" ) {

         protected void onSubmit() {
            try {
               TakePage.this.reservationResult = resourceManager.bookResources( chosen, resv.resv );
               switch( TakePage.this.reservationResult.type ) {
                  case COLLISION:
                     error("Reservation collision!"); break;
                  case NORMAL:
                     info("Succesfully reserved."); break;
                  case MERGED:
                     info("Succesfully merged with current reservations."); break;
               }
            } catch ( UnknownResourceException ex ) {
               error( ex.getMessage() );
            }
         }
      };


      add( form );

      form.add( new ReservationListPanel( "reservationResult", new PropertyModel( this, "reservationResult" ) ) );

      form.add( new FeedbackPanel( "feedback" ) );
      form.add( new TextField( "user", new PropertyModel( resv, "forUser" ) ).setEnabled( false ) );

      form.add( new DateTextField( "from", new PropertyModel( resv, "from" ), DATE_CONV ).add( new DatePicker() ) );
      form.add( new DateTextField( "to",   new PropertyModel( resv, "to" ),   DATE_CONV ).add( new DatePicker() ) );

      form.add( new TextField( "note", new PropertyModel( this, "note" ) ) );

      //.add( new CheckBoxMultipleChoice( "cbmc", new ListModel(this.chosen), resources) )

      // Checkboxes
      final CheckGroup chgrp = new CheckGroup( "chgrp", chosen );
      chgrp.setRenderBodyOnly( false );
      form.add( chgrp.add( new GridView<Resource>( "freeResources", new ListColumnGridDataProvider( resources, 3 ) ) {

         protected void populateEmptyItem( Item<Resource> li ) {
            li.add( new WebMarkupContainer( "check" ).setVisible( false ) );
            li.add( new WebMarkupContainer( "label" ).setVisible( false ) );
         }

         protected void populateItem( Item<Resource> li ) {
            if ( li.getModelObject() == null ) { populateEmptyItem( li ); return; }
            String name = li.getModelObject().getName();
            li.add( new Check( "check", li.getModel(), chgrp ) );
            li.add( new Label( "label", name ) );
         }
      }.setColumns( 3 ) ) );

      // DEBUG - Selected items
      form.add( new ListView<Resource>( "chosen", chosen ) {
         protected void populateItem( ListItem<Resource> li ) {
            li.add( new Label( "item", "" + li.getModelObject() ) );
         }
      } );

   }// const

   
   
   // Get / set
   public String getNote() { return note; }
   public void setNote( String note ) { this.note = note; }
   
   
}// class TakePage

