
package org.jboss.jawabot.web._pg;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.jboss.jawabot.web._base.BaseLayoutPage;


/**
 *
 * @author Ondrej Zizka
 */
public class CheckBoxMultipleChoiceTestPage extends BaseLayoutPage
{
   private static final Logger log = LoggerFactory.getLogger( CheckBoxMultipleChoiceTestPage.class );
  

   // -- Const --
   public CheckBoxMultipleChoiceTestPage(PageParameters parameters) {
      super( parameters );
      
      
      Form form = new Form("form");
      add( form );

      List SITES = Arrays.asList(new String[] { "The Server Side", "Java Lobby", "Java.Net" });
      
      form.add( new CheckBoxMultipleChoice( "cbmc", SITES ) )
         .add( new ListView("items", SITES){

            @Override
            protected void populateItem( ListItem li ) {
               li.add( new Check( "check" ) );
               li.add( new Label( "label", "A-"+new Random().nextInt(255) ) );
            }
         });
     
   }// const

   

}// class TakePage


