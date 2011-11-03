
package org.jboss.jawabot.web._co;

import cz.dynawest.wicket.PageParametersUtil;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.jboss.jawabot.web._pg.UserPage;

/**
 *  Uses only string.
 *  @author Ondrej Zizka
 */
public class UserLinkSimplePanel extends Panel {
   
   public UserLinkSimplePanel( String id, final String userName ) {
      super( id, new Model(userName) );
   }

   @Override
   protected void onInitialize() {
      super.onInitialize();
      String name = ((String)getDefaultModelObject());
      BookmarkablePageLink<String> link = new BookmarkablePageLink<String>( "link", UserPage.class, PageParametersUtil.create("name", name));
      link.add( new Image( "icoType", "User.gif" ) );
      link.add( new Label("label", "" + name ));
      add(link);
   }
   
   
   
}
