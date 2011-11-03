
package org.jboss.jawabot.web._base;


import org.slf4j.Logger; import org.slf4j.LoggerFactory; 
import org.apache.wicket.PageParameters;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebPage;
import org.jboss.jawabot.web.JawaBotSession;
import org.jboss.jawabot.web.WicketApplication;
import org.jboss.jawabot.web._co.menu.MenuPanel;


/**
 *
 * @author Ondrej Zizka
 */
public class BaseLayoutPage_Vut extends WebPage
{
  private static final Logger log = LoggerFactory.getLogger( BaseLayoutPage_Vut.class );

  public BaseLayoutPage_Vut( PageParameters parameters ) {
    super( parameters );
    
    add(JavascriptPackageResource.getHeaderContribution( BaseLayoutPage_Vut.class, "files/jquery.js"));


    //add( new Header( "content" )); // The rest is done in the inherited classes.

    add( new DebugBar("debug") );

    add( new JawaBotTabBarPanel("tabbar") );
            
    add( new MenuPanel("menu") );
    
  }


  public WicketApplication getApp(){
    return (WicketApplication) this.getApplication();
  }


  public JawaBotSession getSession(){
     return (JawaBotSession) super.getSession();
  }
  


}// class BaseLayoutPage
