
package org.jboss.jawabot.web._pg;


import java.util.List;
import java.util.logging.*;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.jboss.jawabot.Resource;
import org.jboss.jawabot.web.JawaBotSession;


/**
 *
 * @author Ondrej Zizka
 */
public class MenuPanel extends Panel
{
   private static final Logger log = Logger.getLogger( MenuPanel.class.getName() );

   
   public MenuPanel( String id ) {
      super( id );
   }

   
   @Override
   protected void onInitialize() {
      super.onInitialize();

      // User box.
      add( new WebMarkupContainer("userNotLogged"){
            @Override public boolean isVisible() { return !isUserLogged(); }
      });
      add( new WebMarkupContainer("userLogged"){
            @Override public boolean isVisible() { return isUserLogged(); }
         }
         .add( new Label("loggedUser", new PropertyModel(getSession(), "loggedUser")){
            @Override public boolean isVisible() { return isUserLogged(); }
         })
      );

      // Resources.
      List<Resource> resources = ((ConveniencePageBase)getPage()).getJawaBot().getResourceManager().getResourcesWithNoReservations();

      add(new ListView<Resource>("resourceList", new ListModel( resources ) ) {
        @Override protected void populateItem(ListItem<Resource> item) {
           item.add( new BookmarkablePageLink("link", ResourcePage.class)
                     .setParameter("name", item.getModelObject().getName())
                     .add( new Label("name", item.getModelObject().getName()) )
           );
        }
      });

   }// onInitialize()

  

  @Override public JawaBotSession getSession(){ return (JawaBotSession) super.getSession(); }
  private boolean isUserLogged(){ return null != getSession().getLoggedUser(); }

}// class MenuPanel
