
package org.jboss.jawabot.web._pg;



import cz.dynawest.util.DateUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.jboss.jawabot.Reservation;
import org.jboss.jawabot.ReservationWrap;


/**
 *
 * @author Ondrej Zizka
 */
public class LeavePage extends BaseLayoutPage
{
   private static final Logger log = Logger.getLogger( TakePage.class.getName() );


   // -- Form backing --

   private List<ReservationWrap> checks = new ArrayList<ReservationWrap>();

   

   // -- Const --
   public LeavePage(PageParameters parameters) {
      super(parameters);

     // Reservations of the given user.
     List<ReservationWrap> freeResources = new ArrayList(3);
     freeResources.add( new ReservationWrap( ReservationWrap.Type.NORMAL, new Reservation("ozizka", DateUtils.fromStringSQL("2010-05-14"), DateUtils.fromStringSQL("2010-05-14")), "jawa05") );
     freeResources.add( new ReservationWrap( ReservationWrap.Type.NORMAL, new Reservation("ozizka", DateUtils.fromStringSQL("2010-05-14"), DateUtils.fromStringSQL("2010-05-15")), "jawa06") );
     freeResources.add( new ReservationWrap( ReservationWrap.Type.NORMAL, new Reservation("ozizka", DateUtils.fromStringSQL("2010-05-16"), DateUtils.fromStringSQL("2010-05-16")), "jawa07") );
     
     final CheckGroup chgrp = new CheckGroup("chgrp", checks);
     chgrp.setRenderBodyOnly(false);

     DateTextField tfFrom, tfTo;

     add( new Form("form"){
            @Override
            protected void onSubmit() {
               super.onSubmit();
               log.info("These resources will be released: " + ArrayUtils.toString(checks));
               // TODO
            }
         }
        .add( chgrp
           .add( new CheckGroupSelector("groupSelector") )
           .add(new ListView<ReservationWrap>("ownedResources", new ListModel( freeResources ) ) {
              @Override protected void populateItem(ListItem<ReservationWrap> item) {
                 ReservationWrap resv = item.getModelObject();
                 item.add( new Check("check", item.getModel(), chgrp) )
                 .add( new Label("res",  new PropertyModel(resv, "resourceName")) )

                 .add( new Label("from", new PropertyModel(resv, "from")) )
                 .add( new Label("to",   new PropertyModel(resv, "to")) )

                 .add( new Label("note", new PropertyModel(resv, "note")) );
              }
           })
        )
     );

      
   }// const

  

}// class TakePage
