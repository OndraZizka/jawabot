
package org.jboss.jawabot.web._pg;


import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.jboss.jawabot.web._base.BaseLayoutPage;
import org.jboss.jawabot.web._co.ResourceDetailPanel;
import org.jboss.jawabot.web._co.ResourcesListPanel;


/**
 *
 * @author Ondrej Zizka
 */
public class ResourcePage extends BaseLayoutPage
{
  private static final Logger log = Logger.getLogger( HomePage.class.getName() );

   // Page params.
   public final static String PARAM_NAME = "name";
  
  
  /** Page const */
  public ResourcePage( PageParameters params ) {
     super(params);
     log.debug(" Page params: " + params );

     String resName = params.getString(PARAM_NAME);
     

     if( resName != null ){
        add( new Label("heading", "Resource "+resName+" - details"));
        add( new ResourceDetailPanel( "resPanel", resName ) );
     }
     else{
        add( new Label("heading", "List of resources"));
        add( new ResourcesListPanel("resPanel") );
     }
  }


}// class HomePage
