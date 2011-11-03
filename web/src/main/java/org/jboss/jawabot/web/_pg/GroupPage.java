
package org.jboss.jawabot.web._pg;


import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.jboss.jawabot.groupmgr.GroupManager;
import org.jboss.jawabot.state.ent.Group;
import org.jboss.jawabot.web._base.BaseLayoutPage;


/**
 *
 * @author Ondrej Zizka
 */
public class GroupPage extends BaseLayoutPage
{
  private static final Logger log = Logger.getLogger( GroupPage.class.getName() );
  private final String GROUP_NAME = "group";
  
  @Inject private GroupManager groupManager;

  
  /** Page const */
  public GroupPage( PageParameters params ) {
     super(params);
     
     log.debug("injected groupManager: " + this.groupManager);
     
     log.debug(" Page params: " + params.toString());
     
     // Resource
     //Group group = JawaBotApp.getJawaBot().getGroupManager().getGroup( params.getString(GROUP_NAME) );
     Group resource = new Group( params.getString(GROUP_NAME) );

     add( new Label("group", resource.getName()) );

  }


}// class HomePage
