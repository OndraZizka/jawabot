
package org.jboss.jawabot.web._base;


import java.util.logging.*;
import org.apache.wicket.PageParameters;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.jboss.jawabot.web.JawaBotSession;
import org.jboss.jawabot.web.WicketApplication;
import org.jboss.jawabot.web._pg.MenuPanel;


/**
 *
 * @author Ondrej Zizka
 */
public class BaseLayoutPage_orig extends ConveniencePageBase
{
  private static final Logger log = Logger.getLogger( BaseLayoutPage_orig.class.getName() );

  public BaseLayoutPage_orig() {
  }

  public BaseLayoutPage_orig( PageParameters parameters ) {
    super( parameters );

    //add( new Header( "content" )); // The rest is done in the inherited classes.

    add( new DebugBar("debug") );

    add( new MenuPanel("menu") );
  }


  public WicketApplication getApp(){
    return (WicketApplication) this.getApplication();
  }


  public JawaBotSession getSession(){
     return (JawaBotSession) super.getSession();
  }
  


}// class BaseLayoutPage
