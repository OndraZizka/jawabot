
package org.jboss.jawabot.web._pg;


import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.jboss.jawabot.web._base.BaseLayoutPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Ondrej Zizka
 */
public class UserPage extends BaseLayoutPage
{
  private static final Logger log = LoggerFactory.getLogger( UserPage.class );

  
   // Page params.
   public final static String PARAM_NAME = "name";
  
  
  /** Page const */
  public UserPage( PageParameters params ) {
     super(params);
     log.debug(" Page params: " + params );

     add( new Label("heading", "List of resources"));
     
     // TODO: Perhaps create a user page aggregating contributions from plugins.
     /*String resName = params.getString(PARAM_NAME);
     Resource res = resourceManager.getResource( resName );
 
     if( res != null ){
        add( new Label("heading", "Resource "+res.getName()+" - details"));
        add( new ResourceDetailPanel( "resPanel", res ) );
     }*/
  }


}// class HomePage
