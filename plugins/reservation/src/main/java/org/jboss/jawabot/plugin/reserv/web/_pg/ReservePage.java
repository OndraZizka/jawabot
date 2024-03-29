
package org.jboss.jawabot.plugin.reserv.web._pg;

import cz.dynawest.util.DateUtils;
import cz.dynawest.wicket.PatternDateConverterThreadLocal;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.PageParameters;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.jboss.jawabot.ex.UnknownResourceException;
import org.jboss.jawabot.plugin.reserv.bus.ReservationWrap;
import org.jboss.jawabot.plugin.reserv.bus.Resource;
import org.jboss.jawabot.plugin.reserv.bus.ResourceManager;
import org.jboss.jawabot.plugin.reserv.bus.ResourceManager.ReservationsBookingResult;
import org.jboss.jawabot.web._base.BaseLayoutPage;
import org.jboss.jawabot.web._pg.HomePage;



/**
 * Reservation creation form.
 * @author Ondrej Zizka
 */
public class ReservePage extends BaseLayoutPage {
    
   @Inject private ResourceManager resourceManager;
   

   // Page params.
   public final static String PARAM_RES = "res";
   public final static String PARAM_FROM_OFFSET = "fromDateOffset";
   public final static String PARAM_TO_OFFSET = "toDateOffset";
   public final static String PARAM_USER = "user"; // optional
   

   // Page state.
   private Resource selectedResource = null;
   private Date dateFrom = null;
   private Date dateTo = null;
   private String owner = null;
   private String note = null;
   
   
   private static PatternDateConverterThreadLocal patternDateConverterTL = new PatternDateConverterThreadLocal("yyyy-MM-dd", true);
   public static PatternDateConverter getPatternDateConverter() { return patternDateConverterTL.get(); }
   

   //<editor-fold defaultstate="collapsed" desc="get set">
   public Date getDateFrom() { return dateFrom; }
   public void setDateFrom( Date dateFrom ) { this.dateFrom = dateFrom; }
   public Date getDateTo() { return dateTo; }
   public void setDateTo( Date dateTo ) { this.dateTo = dateTo; }
   public String getOwner() { return owner; }
   public void setOwner( String owner ) { this.owner = owner; }
   public Resource getSelectedResource() { return selectedResource; }
   public void setSelectedResource( Resource selectedResource ) { this.selectedResource = selectedResource; }
   public String getNote() { return note; }
   public void setNote( String note ) { this.note = note; }
   //</editor-fold>


   public ReservePage( ReservationWrap resv, String note, String ownerName ) {
      this.dateFrom = resv.getFrom();
      this.dateTo = resv.getTo();
      this.owner = resv.getForUser();
      this.selectedResource = resourceManager.getResource( resv.getResourceName() );
      this.note = note;
   }
   
   
   public ReservePage( PageParameters params ) {

      super( params );

      Form form = new Form( "form" );
      this.add(form);


      // Model.
      IModel<List<? extends Resource>> resourceChoices = new AbstractReadOnlyModel<List<? extends Resource>>() {
         @Override public List<Resource> getObject() {
            return resourceManager.getResources_SortByName();
         }
      };

      // Resource select.
      final DropDownChoice<Resource> resourcesSelect = new DropDownChoice<Resource>("resources",
            new PropertyModel<Resource>(this, "selectedResource"), resourceChoices);
      form.add( resourcesSelect );

      // User.
      form.add( new TextField( "owner", new PropertyModel( this, "owner" )) );
      // Date pickers
      PatternDateConverter pdc = this.patternDateConverterTL.get();
      //form.add( new DateTextField("dateFrom", new PropertyModel<Date>( this, "dateFrom" ), pdc ) );
      //form.add( new DateTextField("dateTo",   new PropertyModel<Date>( this, "dateTo" ), pdc ) );
      form.add( new DateTextField( "dateFrom", new PropertyModel<Date>( this, "dateFrom" ), "yyyy-MM-dd"));
      form.add( new DateTextField( "dateTo", new PropertyModel<Date>( this, "dateFrom" ), "yyyy-MM-dd"));

      // Note
      form.add( new TextField( "note", new PropertyModel( this, "note" )) );

      // Submit
      form.add( new Button("submit") );


      

      // Action handling.
      Integer fromOffset = params.getInt(PARAM_FROM_OFFSET, 0);
      Integer toOffset   = params.getAsInteger(PARAM_TO_OFFSET);

      if( null == toOffset ){

         Date today = new Date();
         Date dayFrom = DateUtils.addDays( today, fromOffset );
         Date dayTo   = DateUtils.addDays( today, toOffset );

         try {
            ReservationsBookingResult bookResources =
            resourceManager.bookResources (
                    params.getString(PARAM_RES),
                    params.getString(PARAM_USER),
                    dayFrom, dayTo
            );
            // continue to original requested destination if exists, otherwise go to a default home page
            //if( ! continueToOriginalDestination() ) {
               setResponsePage( HomePage.class );
            //}
         }
         catch ( UnknownResourceException ex ){
            this.error( ex.getMessage() );
         }
      }

   }// const

}// class
