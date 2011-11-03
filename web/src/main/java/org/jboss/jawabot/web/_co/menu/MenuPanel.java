
package org.jboss.jawabot.web._co.menu;


import org.jboss.jawabot.web._base.ConveniencePageBase;
import java.util.List;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.util.ListModel;
import org.jboss.jawabot.Resource;
import org.jboss.jawabot.plugin.pastebin.PasteBinEntry;
import org.jboss.jawabot.state.ent.Group;
import org.jboss.jawabot.web.JawaBotSession;
import org.jboss.jawabot.web._co.GroupLinkPanel;
import org.jboss.jawabot.web._co.PastebinLinkPanel;
import org.jboss.jawabot.web._co.ResourceLinkPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Ondrej Zizka
 */
public class MenuPanel extends Panel
{
   private static final Logger log = LoggerFactory.getLogger( MenuPanel.class );

   
   public MenuPanel( String id ) {
      super( id );
   }

   
   @Override
   protected void onInitialize() {
      super.onInitialize();
      
      // User box - MenuBox test.
      add( new MenuBoxPanel( "accountBox", "Account", new AccountBoxPanel("content") ) );


      // Resources.
      List<Resource> resources = ((ConveniencePageBase)getPage()).getJawaBot().getResourceManager().getResourcesWithNoReservations();

      add(new ListView<Resource>("resourceList", new ListModel( resources ) ) {
        @Override protected void populateItem(ListItem<Resource> item) {
           item.add( new ResourceLinkPanel("link", item.getModelObject()));
        }
      });

      
      // Groups
      List<Group> groups = ((ConveniencePageBase)getPage()).getJawaBot().getGroupManager().getAllGroups_OrderByName();
      
      add(new ListView<Group>("groupList", new ListModel( groups ) ) {
        @Override protected void populateItem(ListItem<Group> item) {
           item.add( new GroupLinkPanel("link", item.getModelObject()));
        }
      });
      
      
      // PasteBin
      List<PasteBinEntry> pastebins = ((ConveniencePageBase)getPage()).getJawaBot().getPasteBinManager().getPastes_OrderByWhenDesc(10);
      
      add(new ListView<PasteBinEntry>("pastebinList", new ListModel( pastebins ) ) {
        @Override protected void populateItem(ListItem<PasteBinEntry> item) {
           item.add( new PastebinLinkPanel("link", item.getModelObject()));
        }
      });


   }// onInitialize()

  

  @Override public JawaBotSession getSession(){ return (JawaBotSession) super.getSession(); }
  private boolean isUserLogged(){ return null != getSession().getLoggedUser(); }

}// class MenuPanel
