
package org.jboss.jawabot.web._co;

import cz.dynawest.wicket.PageParametersUtil;
import javax.inject.Inject;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.jboss.jawabot.state.ent.User;
import org.jboss.jawabot.usermgr.UserManager;
import org.jboss.jawabot.web._pg.UserPage;

/**
 *  Uses User object and UserManager to look up.
 *  @author Ondrej Zizka
 */
public class UserLinkPanel extends Panel {
   
   @Inject UserManager userManager;

   public UserLinkPanel( String id, final long userId ) {
      super( id );
      super.setDefaultModel( new Model( userManager.byID(userId)) );
   }

   public UserLinkPanel( String id, final String userName ) {
      super( id );
      super.setDefaultModel( new Model( userManager.byName(userName)) );
   }

   public UserLinkPanel( String id, final User user ) {
      super( id, new Model(user) );
   }

   @Override
   protected void onInitialize() {
      super.onInitialize();
      User user = ((User)getDefaultModelObject());
      BookmarkablePageLink<User> link = new BookmarkablePageLink<User>( "link", UserPage.class, PageParametersUtil.create("name", user.getName()));
      link.add( new Image( "icoType", "User.gif" ) );
      link.add( new Label("label", "" + user.getName() ));
      add(link);
   }
   
   
   
}
