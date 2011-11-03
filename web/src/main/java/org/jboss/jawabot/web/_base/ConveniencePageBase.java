
package org.jboss.jawabot.web._base;

import org.apache.wicket.IPageMap;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.jboss.jawabot.JawaBot;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.ResourceManager;

/**
 * This abstracts access to this app's managers and makes it easier.
 * @author Ondrej Zizka
 */
public class ConveniencePageBase extends WebPage {



   // Accesssors.
   
   public JawaBot getJawaBot(){
      return JawaBotApp.getJawaBot();
   }

   public ResourceManager getResourceManager(){
      return this.getJawaBot().getResourceManager();
   }



   // Const.

   public ConveniencePageBase( IPageMap pageMap, PageParameters parameters ) {
      super( pageMap, parameters );
   }

   public ConveniencePageBase( PageParameters parameters ) {
      super( parameters );
   }

   public ConveniencePageBase( IPageMap pageMap, IModel<?> model ) {
      super( pageMap, model );
   }

   public ConveniencePageBase( IPageMap pageMap ) {
      super( pageMap );
   }

   public ConveniencePageBase( IModel<?> model ) {
      super( model );
   }

   public ConveniencePageBase() {
   }

}
