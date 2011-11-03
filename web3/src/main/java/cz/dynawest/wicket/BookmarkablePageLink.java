
package cz.dynawest.wicket;


import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;


/**
 *
 * @author Ondrej Zizka
 */
public class BookmarkablePageLink extends org.apache.wicket.markup.html.link.BookmarkablePageLink<String>
{
   public <C extends Page> BookmarkablePageLink(String id, Class<C> pageClass, PageParameters parameters) {
      super(id, pageClass, parameters);
   }

   public <C extends Page> BookmarkablePageLink(String id, Class<C> pageClass) {
      super(id, pageClass);
   }
}// class BookmarkablePageLink
