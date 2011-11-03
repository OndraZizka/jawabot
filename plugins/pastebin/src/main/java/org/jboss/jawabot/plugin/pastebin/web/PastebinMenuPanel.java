
package org.jboss.jawabot.plugin.pastebin.web;


import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.util.ListModel;
import org.jboss.jawabot.FromService;
import org.jboss.jawabot.plugin.pastebin.JpaPasteBinManager;
import org.jboss.jawabot.plugin.pastebin.ent.PasteBinEntry;
import org.jboss.jawabot.plugin.pastebin.web._co.PastebinLinkPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Ondrej Zizka
 */
public class PastebinMenuPanel extends Panel {

    private static final Logger log = LoggerFactory.getLogger( PastebinMenuPanel.class );
    
    
    @Inject @FromService private JpaPasteBinManager pbManager;


    public PastebinMenuPanel( String id ) {
        super( id );
    }


    @Override
    protected void onInitialize() {
        super.onInitialize();


  
      /*add( new Link("new_paste", new Model("") ) {
            public void onClick() {
                this.setResponsePage( PasteBinPage.class );
            }
        } );*/
      
      // ((ConveniencePageBase)getPage()).getJawaBot().getPasteBinManager()
      List<PasteBinEntry> pastebins = pbManager.getLastPastes_OrderByWhenDesc(10);
      
      add(new ListView<PasteBinEntry>("pastebinList", new ListModel( pastebins ) ) {
        @Override protected void populateItem(ListItem<PasteBinEntry> item) {
           item.add( new PastebinLinkPanel("link", item.getModelObject()));
        }
      });



    }// onInitialize()



}// class MenuPanel
