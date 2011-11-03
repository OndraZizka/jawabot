
package org.jboss.jawabot.web._co.menu;


import org.slf4j.Logger; import org.slf4j.LoggerFactory; 
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.jboss.jawabot.web.JawaBotSession;


/**
 *
 * @author Ondrej Zizka
 */
public class MenuBoxPanel extends Panel
{
   private static final Logger log = LoggerFactory.getLogger( MenuBoxPanel.class );
   
   String label;
   //Panel content;

   
   /**
    * 
    * @param id
    * @param label
    * @param content 
    */
   public MenuBoxPanel( String id, String label, Panel content ) {
      super( id );
      this.label = label;
      
      add( new Label("label", this.label ) );
      
      //add( new WebMarkupContainer("content")/*.add(content).setRenderBodyOnly(true)*/   );
      add( content );
      
   }

   
   @Override
   protected void onInitialize() {
      super.onInitialize();


   }// onInitialize()

  

  @Override public JawaBotSession getSession(){ return (JawaBotSession) super.getSession(); }
  private boolean isUserLogged(){ return null != getSession().getLoggedUser(); }

}// class MenuPanel
