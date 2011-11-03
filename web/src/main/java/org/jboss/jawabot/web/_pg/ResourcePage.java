
package org.jboss.jawabot.web._pg;


import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.Resource;
import org.jboss.jawabot.web._base.BaseLayoutPage;
import org.jboss.jawabot.web._co.ResourceDetailPanel;
import org.jboss.jawabot.web._co.ResourcesListPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Ondrej Zizka
 */
public class ResourcePage extends BaseLayoutPage
{
  private static final Logger log = LoggerFactory.getLogger( ResourcePage.class );

  
   // Page params.
   public final static String PARAM_NAME = "name";
  
  
  /** Page const */
  public ResourcePage( PageParameters params ) {
     super(params);
     log.debug(" Page params: " + params );

     String resName = params.getString(PARAM_NAME);
     Resource res = JawaBotApp.getJawaBot().getResourceManager().getResource( resName );
 
     if( res != null ){
        add( new Label("heading", "Resource "+res.getName()+" - details"));
        add( new ResourceDetailPanel( "resPanel", res ) );
     }
     else{
        add( new Label("heading", "List of resources"));
        add( new ResourcesListPanel("resPanel") );
     }
  }


}// class HomePage
