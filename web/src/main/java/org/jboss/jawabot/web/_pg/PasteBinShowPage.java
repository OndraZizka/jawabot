
package org.jboss.jawabot.web._pg;

import cz.dynawest.util.DateUtils;
import cz.dynawest.wicket.WMC;
import javax.inject.Inject;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.jboss.jawabot.plugin.irc.web._co.ChannelLinkSimplePanel;
import org.jboss.jawabot.plugin.pastebin.JpaPasteBinManager;
import org.jboss.jawabot.plugin.pastebin.PasteBinEntry;
import org.jboss.jawabot.web._base.BaseLayoutPage;
import org.jboss.jawabot.web._co.UserLinkSimplePanel;


/** 
 *  
 *  @author Ondrej Zizka
 */
public class PasteBinShowPage extends BaseLayoutPage {
   
   @Inject JpaPasteBinManager pbManager;
   
   
   public PasteBinShowPage( PageParameters params ) {
      super(params);
      
      PasteBinEntry pbe = null;
      
      if( params.containsKey("id") ){
         pbe = pbManager.getById( params.getLong("id") );
         // To distinguish undefined input and not-found.
         if( pbe == null )  pbe = PasteBinEntry.NON_EXISTENT;
      }
      
      this.setDefaultModel( new Model( pbe ) );
   }

   public PasteBinShowPage( PasteBinEntry entry ) {
      this.setDefaultModel( new Model( entry ) );
   }


   @Override
   protected void onInitialize() {
      super.onInitialize();
      
      PasteBinEntry entry = (PasteBinEntry) this.getDefaultModelObject();
      
      WMC entryComp = new WMC("entry");
      add( entryComp.setVisible( false ) );
      
      if( entry != null &&  entry != PasteBinEntry.NON_EXISTENT ){
         entryComp.setVisible(true);
         entryComp.add( new UserLinkSimplePanel( "author", new PropertyModel( entry, "author" ) ) );
         entryComp.add( new WMC( "hasFor" ).add(
                 new UserLinkSimplePanel( "for", new PropertyModel( entry, "for" ) )
              ).setVisible( entry.hasAuthor() ) );
         entryComp.add( new Label( "when", DateUtils.toStringSQL( entry.getWhen() ) ) );
         entryComp.add( new WMC( "hasChannel" ).add(
                 new ChannelLinkSimplePanel( "channel", entry.getChannel() )
              ).setVisible( entry.hasChannel() ) );
         entryComp.add( new MultiLineLabel( "text", new PropertyModel( entry, "text" ) ) );
      }
      
      add( new WMC("noInput").setVisible( entry == null ) );
      
      add( new WMC("entry404").setVisible( entry == PasteBinEntry.NON_EXISTENT ) );
         
   }
   
}// class
