
package org.jboss.jawabot.web._pg;


import javax.inject.Inject;
import org.slf4j.Logger; import org.slf4j.LoggerFactory; 
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.jboss.jawabot.usermgr.UserManager;
import org.jboss.jawabot.web._base.BaseLayoutPage;



/**
 *
 * @author Ondrej Zizka
 */
public class HomePage extends BaseLayoutPage
{
  private static final Logger log = LoggerFactory.getLogger( HomePage.class );
  
  @Inject private UserManager userManager;


  private String note = "";



  public HomePage( PageParameters parameters ) {
     super(parameters);
     
     final boolean logged = getSession().isUserLogged();
     
     // Not logged warning
     add( new Label("notLoggedWarn", new ResourceModel("notLoggedWarn")).setVisibilityAllowed( !logged ) );

  }



}// class HomePage
