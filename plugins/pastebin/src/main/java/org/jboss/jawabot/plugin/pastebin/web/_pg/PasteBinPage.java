
package org.jboss.jawabot.plugin.pastebin.web._pg;


import cz.dynawest.util.DateUtils;
import java.util.List;
import javax.inject.Inject;
import org.slf4j.Logger; import org.slf4j.LoggerFactory; 
import org.apache.wicket.PageParameters;
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
import org.jboss.jawabot.plugin.pastebin.JpaPasteBinManager;
import org.jboss.jawabot.plugin.pastebin.ent.PasteBinEntry;
import org.jboss.jawabot.web._base.BaseLayoutPage;
import org.jboss.jawabot.plugin.irc.web._co.ChannelLinkSimplePanel;
import org.jboss.jawabot.web._co.UserLinkSimplePanel;



/**
 *  Page with form to submit new paste entry and a list of recent entries.
 * 
 *  @author Ondrej Zizka
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

   /**
    * Builds page components.
    */
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
      //IModel model = new ListModel<PasteBinEntry>( entries );
      IModel model = new LoadableDetachableModel<List<? extends PasteBinEntry>>() {
         protected List<? extends PasteBinEntry> load() {
            return pbManager.getLastPastes_OrderByWhenDesc(100);
         }
      };

      add( new WebMarkupContainer( "entries" )
         
         .add( new ListView<PasteBinEntry>( "entry", model) {

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





}// class HomePage
