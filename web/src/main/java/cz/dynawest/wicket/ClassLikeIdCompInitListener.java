package cz.dynawest.wicket;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.application.IComponentInitializationListener;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.model.Model;

/**
 * Adds class="..." with value of wicket:id="...".
 * Usage: 
 *     Application.addComponentInitializationListener( new ClassLikeIdCompInitListener() );
 * 
 * @author Ondrej Zizka
 */
public class ClassLikeIdCompInitListener implements IComponentInitializationListener {

   @Override
   public void onInitialize( Component comp ) {
      if( !( comp instanceof MarkupContainer ) )
         return;
      
      MarkupContainer mc = ( MarkupContainer ) comp;
      mc.add( new AttributeAppender( "class", new Model( mc.getId() ), " ") );
   }
   
}// class

