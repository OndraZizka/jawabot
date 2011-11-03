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


/**
 *  
 *  @author Ondrej Zizka
 */
public class JawaBotTabBarPanel extends Panel {
   
   
   @Inject Instance<ITabBarContrib> tabsInstances;

   
   public JawaBotTabBarPanel( String id ) {
      super(id);
      this.setRenderBodyOnly(true);
   }

   @Override
   protected void onInitialize() {
      super.onInitialize();
      
      /*add( new RepeatingView("tabs"){
         protected void onPopulate() 
         {
            // Add all plugins' tabs with a link to it's home page.
            for( ITabBarContrib tabContrib : tabsInstances ) {
               BookmarkablePageLink link = new BookmarkablePageLink("link", tabContrib.getLinkedPage());
               link.add( tabContrib.getLabel("label") );
               this.add( link );
            }
            this.add( new BookmarkablePageLink("link", HomePage.class).add(new Label("label", "HP")) );
         }
      });*/

      RepeatingView view = new RepeatingView("tabs");
      this.add( view );
      
      view.add( new WMC( view.newChildId() )
              .add( new BookmarkablePageLink("link", HomePage.class)
                         .add(new Label("label", "HP")) 
              )
      );
      
      // Add all plugins' tabs with a link to it's home page.
      for( ITabBarContrib tabContrib : tabsInstances ) {
         view.add( new WMC( view.newChildId() )
                 .add( new BookmarkablePageLink("link", tabContrib.getLinkedPage() )
                     .add( tabContrib.getLabel("label"))
                 )
         );

         BookmarkablePageLink link = new BookmarkablePageLink("link", tabContrib.getLinkedPage());
         link.add( tabContrib.getLabel("label") );
         this.add( link );
      }

      
      
   }// onInitialize()
   
}// class

