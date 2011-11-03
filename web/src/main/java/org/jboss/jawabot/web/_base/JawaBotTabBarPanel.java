package org.jboss.jawabot.web._base;

import cz.dynawest.wicket.WMC;
import org.jboss.jawabot.mod.web.ITabBarContrib;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.jboss.jawabot.web._pg.HomePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  
 *  @author Ondrej Zizka
 */
public class JawaBotTabBarPanel extends Panel {
   private static final Logger log = LoggerFactory.getLogger( JawaBotTabBarPanel.class );
   
   
   @Inject Instance<ITabBarContrib> tabsInstances;

   
   public JawaBotTabBarPanel( String id ) {
      super(id);
      this.setRenderBodyOnly(true);
   }

   @Override
   protected void onInitialize() {
      super.onInitialize();
      
      RepeatingView view = new RepeatingView("tabs");
      this.add( view );
      
      // JawaBot's home page.
      view.add( new WMC( view.newChildId() )
              .add( new BookmarkablePageLink("link", HomePage.class)
                         .add(new Label("label", "Home")) 
              )
      );
      
      // Add all plugins' tabs with a link to it's home page.
      for( ITabBarContrib tabContrib : this.tabsInstances ) {
         log.debug(" TabContrib: " + tabContrib + " - " + tabContrib.getLinkedPage().getSimpleName() );
         view.add( new WMC( view.newChildId() )
                 .add( new BookmarkablePageLink("link", tabContrib.getLinkedPage() )
                     .add( tabContrib.getLabel("label"))
                 )
         );
      }

      
      
   }// onInitialize()
   
}// class

