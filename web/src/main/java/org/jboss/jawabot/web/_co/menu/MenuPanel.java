
package org.jboss.jawabot.web._co.menu;


import cz.dynawest.wicket.WMC;
import org.apache.wicket.markup.html.panel.Panel;
import org.jboss.jawabot.web.JawaBotSession;
import org.jboss.jawabot.web._base.BaseLayoutPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  Global menu panel - container for plugins' menu contributions.
 *  TODO: Implement method to let plugins contribute.
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
      
      // User account box - login etc.
      add( new MenuBoxPanel( "accountBox", "Account", new AccountBoxPanel("content") ) );

      
      // Current plugin's menu.
      Panel menuPanel = null;
      if( this.getPage() instanceof BaseLayoutPage ){
         BaseLayoutPage page = (BaseLayoutPage) this.getPage();
         menuPanel = page.getMenuPanel("currentPluginMenu");
      }
      if( menuPanel != null )  add( menuPanel );
      else add( new WMC("currentPluginMenu").setVisibilityAllowed(false) );

      
      

      // Other plugins' menu contributions.
      add( new WMC("contributions") );
      
      
      
      // Groups.
      
      //List<Group> groups = ((ConveniencePageBase)getPage()).getJawaBot().getGroupManager().getAllGroups_OrderByName();
      //List<Group> groups = this.groupManager.getAllGroups_OrderByName();
      
      /*add(new ListView<Group>("groupList", new ListModel( groups ) ) {
        @Override protected void populateItem(ListItem<Group> item) {
           item.add( new GroupLinkPanel("link", item.getModelObject()));
        }
      });/**/
      
      

   }// onInitialize()

  

  @Override public JawaBotSession getSession(){
     return (JawaBotSession) super.getSession();
  } // TODO: CDI.
  
  
  private boolean isUserLogged(){
     return null != getSession().getLoggedUser();
  }

  

}// class MenuPanel
