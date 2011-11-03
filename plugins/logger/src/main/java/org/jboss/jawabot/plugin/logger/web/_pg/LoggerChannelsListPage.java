
package org.jboss.jawabot.plugin.logger.web._pg;


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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.Reservation;
import org.jboss.jawabot.Resource;
import org.jboss.jawabot.plugin.pastebin.JpaPasteBinManager;
import org.jboss.jawabot.plugin.pastebin.ent.PasteBinEntry;
import org.jboss.jawabot.resmgr.ResourceWithNearestFreePeriodDTO;
import org.jboss.jawabot.state.ent.User;
import org.jboss.jawabot.web._base.BaseLayoutPage;
import org.jboss.jawabot.plugin.irc.web._co.ChannelLinkSimplePanel;
import org.jboss.jawabot.plugin.logger.web._co.ChannelLogLinkSimplePanel;
import org.jboss.jawabot.web._co.UserLinkSimplePanel;
import org.jboss.jawabot.web._pg.PasteBinShowPage;



/**
 *  Page with form to submit new paste entry.
 * 
 * @author Ondrej Zizka
 */
public class LoggerChannelsListPage extends BaseLayoutPage
{
   private static final Logger log = LoggerFactory.getLogger( LoggerChannelsListPage.class );
   
   @Inject private JpaPasteBinManager pbManager;


   // This page's model.
   private PasteBinEntry entry = new PasteBinEntry();


   public LoggerChannelsListPage( PageParameters parameters ) {
      super( parameters );
      build();
   }

   private void build(){

      // Recent entries.
      IModel model = new LoadableDetachableModel<List<? extends PasteBinEntry>>() {
         protected List<? extends PasteBinEntry> load() {
            return pbManager.getLastPastes_OrderByWhenDesc(100);
         }
      };

      add( new WebMarkupContainer( "channels" )
         
         .add( new ListView<PasteBinEntry>( "channel", model) {

            protected void populateItem( final ListItem<PasteBinEntry> item ) {
               final PasteBinEntry entry = item.getModelObject();
               item.add( new ChannelLogLinkSimplePanel( "name", entry.getChannel() ) );
               item.add( new Label( "since", DateUtils.toStringSQL( entry.getWhen() ) ) );
               item.add( new Label( "until", DateUtils.toStringSQL( entry.getWhen() ) ) );
            }// populateItem()
         } )// ListView
      );

   }// build()




}// class HomePage
