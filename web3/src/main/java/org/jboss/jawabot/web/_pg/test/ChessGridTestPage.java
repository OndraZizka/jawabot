
package org.jboss.jawabot.web._pg.test;

import cz.dynawest.wicket.ListColumnGridDataProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.wicket.markup.repeater.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.repeater.data.GridView;
import org.apache.wicket.model.Model;
import org.jboss.jawabot.web._base.BaseLayoutPage;


/**
 *
 * @author Ondrej Zizka
 */
public class ChessGridTestPage extends BaseLayoutPage
{
   private static final Logger log = LoggerFactory.getLogger( CheckBoxMultipleChoiceTestPage.class );
  

   // -- Const --
   public ChessGridTestPage(PageParameters parameters) {
      super( parameters );
      
      
      List SITES = Arrays.asList( new String[]{ "Aaaa 1", "Aaaa 2", "Aaaa 3", "Aaaa 4", "Aaaa 5" } );


      add( new GridView<String>( "chess", new ListColumnGridDataProvider(SITES).setColumns(3) ) {

         @Override
         protected void populateEmptyItem( Item<String> item ) {
            item.add( new WebMarkupContainer("check").setVisible(false) );
            item.add( new Label("label", "-empty-" ) );
         }

         @Override
         protected void populateItem( Item<String> item ) {
            item.add( new CheckBox("check", new Model( new Random().nextBoolean() ) ) );
            item.add( new Label("label", item.getModel() ) );
         }
         
      }.setColumns(3) );
              

   }// const

   

}// class TakePage


