
package org.jboss.jawabot.web._pg;


import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.groupmgr.Group;
import org.jboss.jawabot.web._base.BaseLayoutPage;


/**
 *
 * @author Ondrej Zizka
 */
public class GroupPage extends BaseLayoutPage
{
  private static final Logger log = Logger.getLogger( HomePage.class.getName() );
  private final String GROUP_NAME = "group";

  
  /** Page const */
  public GroupPage( PageParameters params ) {
     super(params);
     
     log.debug(" Page params: " + params.toString() );
     
     // Resource
     //Group group = JawaBotApp.getJawaBot().getGroupManager().getGroup( params.getString(GROUP_NAME) );
     Group resource = new Group( params.getString(GROUP_NAME) );

     add( new Label("group", resource.getName()) );

  }


}// class HomePage
