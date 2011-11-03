
package org.jboss.jawabot.web._pg.test;

import cz.dynawest.util.DateUtils;
import cz.dynawest.wicket.ListColumnGridDataProvider;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.apache.wicket.PageParameters;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.GridView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.ReservationWrap;
import org.jboss.jawabot.Resource;
import org.jboss.jawabot.web._base.BaseLayoutPage;


/**
 *
 * @author Ondrej Zizka
 */
public class CheckBoxWrapListTestPage extends BaseLayoutPage
{
   private static final Logger log = LoggerFactory.getLogger( CheckBoxWrapListTestPage.class );


   // -- Form backing --

   private ReservationWrap resv = new ReservationWrap("ozizka", new Date(), DateUtils.addDays( new Date(), 1 ), null);

   //private List<CheckBoxWrap<Resource>> checks = new ArrayList();
   private CheckBoxWrapList<Resource> checks = new CheckBoxWrapList();
   

   // Note
   private String note;
   public String getNote() {      return note;   }
   public void setNote(String note) {      this.note = note;   }
   
   

   // -- Const --
   public CheckBoxWrapListTestPage(PageParameters parameters) {
      super(parameters);
      
      
      // Current user as default.
      this.resv.setForUser( StringUtils.defaultString( this.getSession().getLoggedUser() ) );

      
      // Free resources.
      //List<Resource> resources = JawaBotApp.getJawaBot().getResourceManager().getResources_SortByName();
      CheckBoxWrapList<Resource> resources = new CheckBoxWrapList<Resource>( JawaBotApp.getJawaBot().getResourceManager().getResources_SortByName() );

      final CheckGroup chgrp = new CheckGroup("chgrp", checks);
      chgrp.setRenderBodyOnly(false);
      


      add( new Form("form")
        .add( new FeedbackPanel("feedback") )
        .add( new TextField("user", new PropertyModel(resv, "forUser")).setEnabled(false) )

        //.add( tfFrom = new TextField("from", new PropertyModel(resv, "from"), Date.class) )
          //.add(new DatePicker("fromPick", tfFrom))
        //.add( tfTo   = new TextField("to", new PropertyModel(resv, "to"), Date.class) )
          //.add(new DatePicker("toPick", tfTo))

        .add( new DateTextField("from", new PropertyModel(resv, "from"), new StyleDateConverter("S-", true)).add(new DatePicker()) )
        .add( new DateTextField("to", new PropertyModel(resv, "to"), new StyleDateConverter("S-", true)).add(new DatePicker()) )

        .add( new TextField("note", new PropertyModel(this, "note")) )
        
        //.add( new CheckBoxMultipleChoice )
              
        // Checkboxes
        .add( chgrp
           /*.add(new ListView<CheckBoxWrap<Resource>>("freeResources", new ListModel( resources ) ) 
           {
              @Override protected void populateItem(ListItem<CheckBoxWrap<Resource>> li)
              {
                 //String name = li.getModelObject().getItem().getName();
                 li.add( new Check("check", new PropertyModel(li.getModelObject(), "checked"), chgrp) );
                 li.add( new Label("label", new PropertyModel(li.getModelObject(), "item.name") ));
              }
           })*/
            .add( new GridView<CheckBoxWrap<Resource>>( "freeResources", new ListColumnGridDataProvider(resources).setColumns(3) ) {

               @Override
               protected void populateEmptyItem( Item<CheckBoxWrap<Resource>> li ) {
                  li.add( new WebMarkupContainer("check").setVisible(false) );
                  li.add( new WebMarkupContainer("label").setVisible(false) );
               }

               @Override
               protected void populateItem( Item<CheckBoxWrap<Resource>> li ) {
                 if( li.getModelObject() == null ){ populateEmptyItem( li ); return; }
                 li.add( new Check("check", new PropertyModel(li.getModelObject(), "checked"), chgrp) );
                 li.add( new Label("label", new PropertyModel(li.getModelObject(), "item.name") ));
               }

            }.setColumns(3) )

        )
     );

      
   }// const

   

}// class TakePage




/**
 *  Wrapper for items of lists with CheckBoxes.
 */
class CheckBoxWrap<T extends Serializable> implements Serializable {

   private boolean checked;
   private T item;

   public CheckBoxWrap( boolean checked, T item ) {
      this.checked = checked;
      this.item = item;
   }

   public boolean isChecked() { return checked; }
   public void setChecked( boolean checked ) { this.checked = checked; }
   public T getItem() { return item; }
   public void setItem( T item ) { this.item = item; }

   @Override public String toString() { return "("+this.getItem()+")"; }
   
   
}


/**
 *  Wrapper for lists with CheckBoxes.
 */
class CheckBoxWrapList<T extends Serializable> extends ArrayList<CheckBoxWrap<T>> implements Serializable {

   public CheckBoxWrapList() {
   }
   
   public CheckBoxWrapList( List<T> items ) {
      this.doImport( items );
   }
   
   public void doImport( List<T> items ){
      for ( T item : items ) {
         this.add( new CheckBoxWrap<T>( true, item ) );
      }      
   }
   
   public List<T> doExport(){
      ArrayList checkedItems = new ArrayList();
      for ( CheckBoxWrap<T> wrap : this ) {
         if( wrap.isChecked() )
            checkedItems.add( wrap.getItem() );
      }
      return checkedItems;
   }
   
}
