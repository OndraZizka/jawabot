
package org.jboss.jawabot.web._co.menu;


import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.jboss.jawabot.web.JawaBotSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Ondrej Zizka
 */
public class AccountBoxPanel extends Panel
{
   private static final Logger log = LoggerFactory.getLogger( MenuPanel.class );

   
   public AccountBoxPanel( String id ) {
      super( id );
   }

   public AccountBoxPanel() {
      super( null );
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
         .add( new Label("loggedUser", new PropertyModel(AccountBoxPanel.this, "session.loggedUser")){
            @Override public boolean isVisible() { return isUserLogged(); }
         })
      );



   }// onInitialize()

  

  @Override public JawaBotSession getSession(){ return (JawaBotSession) super.getSession(); }
  private boolean isUserLogged(){ return null != getSession().getLoggedUser(); }

}// class MenuPanel
