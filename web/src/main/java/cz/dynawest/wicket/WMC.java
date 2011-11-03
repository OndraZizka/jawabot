package cz.dynawest.wicket;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

/**
 *  Alias for WebMarkupContainer.
 *  @author Ondrej Zizka
 */
public class WMC extends WebMarkupContainer {

   public WMC( String id, IModel<?> model ) {
      super( id, model );
   }

   public WMC( String id ) {
      super( id );
   }
   
}// class

