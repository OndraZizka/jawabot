
package org.jboss.jawabot.web._pg;


import cz.dynawest.util.DateUtils;
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
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.string.Strings;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.Reservation;
import org.jboss.jawabot.Resource;
import org.jboss.jawabot.pastebin.PasteBinEntry;
import org.jboss.jawabot.resmgr.ResourceWithNearestFreePeriodDTO;
import org.jboss.jawabot.state.ent.User;
import org.jboss.jawabot.web._base.BaseLayoutPage;



/**
 *
 * @author Ondrej Zizka
 */
public class PasteBinPage extends BaseLayoutPage
{
   private static final Logger log = Logger.getLogger( PasteBinPage.class.getName() );


   // This page's model.
   private PasteBinEntry entry;


   public PasteBinPage( PageParameters parameters ) {
      super( parameters );
      build();
   }

   private void build(){

      // New entry.
      Form form = new Form( "form", new CompoundPropertyModel( this.entry ) ){
         @Override protected void onSubmit() {
            //setResponsePage( new PasteBinPage( new PageParameters() ));

         }
      };
      this.add( form );
      form.add( new TextField( "by" ) );
      form.add( new TextField( "to" ) );
      form.add( new TextArea( "text" ) );
      form.add( new Button("submit") );


      // Recent entries.
      List<PasteBinEntry> entries = JawaBotApp.getPasteBinManager().getAll();

      add( new ListView<PasteBinEntry>( "entries", new ListModel<PasteBinEntry>( entries ) ) {

         @Override
         protected void populateItem( final ListItem<PasteBinEntry> item ) {
            final PasteBinEntry entry = item.getModelObject();
            item.add( new Link("entry"){
               {
                  add( new Label( "by", entry.getBy() ) );
                  add( new Label( "for", entry.getFor() ) );
                  add( new Label( "from", DateUtils.toStringSQL( entry.getWhen() ) ) );
                  add( new Label( "channel", entry.getChannel() ) );
               }

               @Override
               public void onClick() {
                  setResponsePage( new PasteBinShowPage( item.getModelObject() ) );
               }
               
            });
         }// populateItem()
      } );

   }// build()





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
