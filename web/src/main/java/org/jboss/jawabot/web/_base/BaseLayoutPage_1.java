
package org.jboss.jawabot.web._base;


import java.util.logging.*;
import org.apache.wicket.PageParameters;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.jboss.jawabot.web.JawaBotSession;
import org.jboss.jawabot.web.WicketApplication;


/**
 *
 * @author Ondrej Zizka
 */
public class BaseLayoutPage_1 extends ConveniencePageBase
{
  private static final Logger log = Logger.getLogger( BaseLayoutPage.class.getName() );

  public BaseLayoutPage_1( PageParameters parameters ) {
    super( parameters );

    add( new DebugBar("debug") );
  }


  public WicketApplication getApp(){
    return (WicketApplication) this.getApplication();
  }


  public JawaBotSession getSession(){
     return (JawaBotSession) super.getSession();
  }
  


}// class BaseLayoutPage
