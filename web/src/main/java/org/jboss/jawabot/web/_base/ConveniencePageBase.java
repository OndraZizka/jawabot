
package org.jboss.jawabot.web._base;

import org.apache.wicket.IPageMap;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.jboss.jawabot.JawaBot;
import org.jboss.jawabot.JawaBotApp;


/**
 * This abstracts access to this app's managers and makes it easier.
 * @author Ondrej Zizka
 * @deprecated in favor of CDI
 */
public class ConveniencePageBase extends WebPage {


   // Accesssors.
   
   public JawaBot getJawaBot(){
      return JawaBotApp.getJawaBot();
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
