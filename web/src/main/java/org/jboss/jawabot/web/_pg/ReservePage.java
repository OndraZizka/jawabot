
package org.jboss.jawabot.web._pg;

import cz.dynawest.util.DateUtils;
import java.util.Date;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.Resource;
import org.jboss.jawabot.ResourceManager.ReservationsBookingResult;
import org.jboss.jawabot.ex.UnknownResourceException;
import org.jboss.jawabot.web.WicketApplication;
import org.jboss.jawabot.web._base.BaseLayoutPage;

/**
 * Reservation creation form.
 * @author ondra
 */
public class ReservePage extends BaseLayoutPage {

   private Resource selectedResource = null;
   private Date dateFrom = null;
   private Date dateTo = null;
   private String owner = null;
   private String note = null;

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


   public ReservePage( PageParameters params ) {

      super( params );

      Form form = new Form( "form" );
      this.add(form);


      // Model.
      IModel<List<? extends Resource>> resourceChoices = new AbstractReadOnlyModel<List<? extends Resource>>() {
         @Override public List<Resource> getObject() {
            return JawaBotApp.getJawaBot().getResourceManager().getResources_SortByName();
         }
      };

      // Resource select.
      final DropDownChoice<Resource> resourcesSelect = new DropDownChoice<Resource>("resources",
            new PropertyModel<Resource>(this, "selectedResource"), resourceChoices);
      form.add( resourcesSelect );

      // User.
      form.add( new TextField( "owner", new PropertyModel( this, "owner" )) );
      // Date pickers
      PatternDateConverter pdc = WicketApplication.getPatternDateConverterTL().get();
      //form.add( new DateTextField("dateFrom", new PropertyModel<Date>( this, "dateFrom" ), pdc ) );
      //form.add( new DateTextField("dateTo",   new PropertyModel<Date>( this, "dateTo" ), pdc ) );
      form.add( new org.apache.wicket.extensions.markup.html.form.DateTextField( "dateFrom", new PropertyModel<Date>( this, "dateFrom" ), "yyyy-MM-dd"));
      form.add( new org.apache.wicket.extensions.markup.html.form.DateTextField( "dateTo", new PropertyModel<Date>( this, "dateFrom" ), "yyyy-MM-dd"));

      // Note
      form.add( new TextField( "note", new PropertyModel( this, "note" )) );

      // Submit
      form.add( new Button("submit") );


      

      // Action handling.
      Integer fromOffset = params.getInt("fromDateOffset", 0);
      Integer toOffset   = params.getAsInteger("toDateOffset");

      if( null == toOffset ){

         Date today = new Date();
         Date dayFrom = DateUtils.addDays( today, fromOffset );
         Date dayTo   = DateUtils.addDays( today, toOffset );

         try {
            ReservationsBookingResult bookResources =
            JawaBotApp.getJawaBot().getResourceManager().bookResources(
                    params.getString("res"),
                    params.getString("user"),
                    dayFrom, dayTo
            );
            // continue to original requested destination if exists, otherwise go to a default home page
            //if (!continueToOriginalDestination()) {
               setResponsePage( HomePage.class );
            //}
         }
         catch ( UnknownResourceException ex ){
            this.error( ex.getMessage() );
         }
      }

   }// const

}// class
