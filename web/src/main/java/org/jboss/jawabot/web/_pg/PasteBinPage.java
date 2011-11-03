
package org.jboss.jawabot.web._pg;


import cz.dynawest.util.DateUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import org.slf4j.Logger; import org.slf4j.LoggerFactory; 
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
import org.jboss.jawabot.plugin.pastebin.JpaPasteBinManager;
import org.jboss.jawabot.plugin.pastebin.PasteBinEntry;
import org.jboss.jawabot.resmgr.ResourceWithNearestFreePeriodDTO;
import org.jboss.jawabot.state.ent.User;
import org.jboss.jawabot.web._base.BaseLayoutPage;
import org.jboss.jawabot.plugin.irc.web._co.ChannelLinkPanel;
import org.jboss.jawabot.plugin.irc.web._co.ChannelLinkSimplePanel;
import org.jboss.jawabot.web._co.UserLinkSimplePanel;



/**
 *  Page with form to submit new paste entry.
 * 
 * @author Ondrej Zizka
 */
public class PasteBinPage extends BaseLayoutPage
{
   private static final Logger log = LoggerFactory.getLogger( PasteBinPage.class );
   
   @Inject private JpaPasteBinManager pbManager;


   // This page's model.
   private PasteBinEntry entry = new PasteBinEntry();


   public PasteBinPage( PageParameters parameters ) {
      super( parameters );
      build();
   }

   private void build(){

      // New entry.
      Form form = new Form( "form", new CompoundPropertyModel( this.entry ) ){
         @Override protected void onSubmit() {
            pbManager.addEntry(entry);
            //setResponsePage( new PasteBinPage( new PageParameters() ));
            entry.setId(null);
            entry.setText("");
         }
      };
      this.add( form );
      form.add( new TextField( "author" ) );
      form.add( new TextField( "for" ) );
      form.add( new TextArea( "text" ) );
      form.add( new Button("submit") );


      // Recent entries.
      //List<PasteBinEntry> entries = JawaBotApp.getPasteBinManager().getAll();
      List<PasteBinEntry> entries = pbManager.getLastPastes_OrderByWhenDesc(100);

      add( new WebMarkupContainer( "entries" )
         .add( new ListView<PasteBinEntry>( "entry", new ListModel<PasteBinEntry>( entries ) ) {

            @Override
            protected void populateItem( final ListItem<PasteBinEntry> item ) {
               final PasteBinEntry entry = item.getModelObject();
               item.add( new UserLinkSimplePanel( "author", entry.getAuthor() ) );
               item.add( new UserLinkSimplePanel( "for", entry.getFor() ) );
               item.add( new ChannelLinkSimplePanel( "channel", entry.getChannel() ) );
               item.add( new Label( "when", DateUtils.toStringSQL( entry.getWhen() ) ) );
               item.add( new Link( "showLink" ){
                  public void onClick() {
                     setResponsePage( new PasteBinShowPage( item.getModelObject() ) );
                  }
               });
            }// populateItem()
         } )// ListView
      );

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

            List<User> users = JawaBotApp.getUserManager().getUsersRange_OrderByName(0, 1000);
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
