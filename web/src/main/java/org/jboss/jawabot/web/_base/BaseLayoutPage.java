
package org.jboss.jawabot.web._base;


import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.panel.Panel;


/**
 * A class to simply switch the layout page class in a single place.
 * 
 * @author Ondrej Zizka
 */
public abstract class BaseLayoutPage extends BaseLayoutPage_Vut
{
  public BaseLayoutPage( PageParameters parameters ) {
    super( parameters );
  }

  public BaseLayoutPage() {
    super( PageParameters.NULL );
  }
  
  /**
   *  Override this if you want the page to show it's menu.
   *  @param id  Use as the ID of returned component.
   *  @returns  A menu panel. Should have reasonable content (narrow and not much).
   *            BaseLayoutPage returns empty Panel.
   */
  public Panel getMenuPanel( String id ){
     return null;
  }

}// class BaseLayoutPage
