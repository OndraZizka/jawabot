
package org.jboss.jawabot.web._pg;



import cz.dynawest.util.DateUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.apache.wicket.PageParameters;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.jboss.jawabot.JawaBotApp;
import org.jboss.jawabot.ReservationWrap;
import org.jboss.jawabot.Resource;
import org.jboss.jawabot.web._base.BaseLayoutPage;


/**
 *
 * @author Ondrej Zizka
 */
public class TakePage extends BaseLayoutPage
{
   private static final Logger log = LoggerFactory.getLogger( TakePage.class );


   // -- Form backing --

   private List<Boolean> checks = new ArrayList<Boolean>();
   private ReservationWrap resv = new ReservationWrap("ozizka", new Date(), DateUtils.addDays( new Date(), 1 ), null);

   // Note
   private String note;
   public String getNote() {      return note;   }
   public void setNote(String note) {      this.note = note;   }
   

   // -- Const --
   public TakePage(PageParameters parameters) {
      super(parameters);

     // Free resources.
     List<Resource> resources = JawaBotApp.getJawaBot().getResourceManager().getResources_SortByName();

     final CheckGroup chgrp = new CheckGroup("chgrp", checks);

     DateTextField tfFrom, tfTo;

     add( new Form("form")
        .add( new TextField("user", new PropertyModel(resv, "forUser")) )

        //.add( tfFrom = new TextField("from", new PropertyModel(resv, "from"), Date.class) )
          //.add(new DatePicker("fromPick", tfFrom))
        //.add( tfTo   = new TextField("to", new PropertyModel(resv, "to"), Date.class) )
          //.add(new DatePicker("toPick", tfTo))

        .add( new DateTextField("from", new PropertyModel(resv, "from"), new StyleDateConverter("S-", true)).add(new DatePicker()) )
        .add( new DateTextField("to", new PropertyModel(resv, "to"), new StyleDateConverter("S-", true)).add(new DatePicker()) )

        .add( new TextField("note", new PropertyModel(this, "note")) )
        .add( chgrp
           .add(new ListView<Resource>("freeResources", new ListModel( resources ) ) {
              @Override protected void populateItem(ListItem<Resource> item) {
                 String name = item.getModelObject().getName();
                 item.add( new Check("check", item.getModel(), chgrp) )
                 .add( new Label("label", new PropertyModel(item.getModel(), "name") ));
              }
           })
        )
     );

      
   }// const

  

}// class TakePage
