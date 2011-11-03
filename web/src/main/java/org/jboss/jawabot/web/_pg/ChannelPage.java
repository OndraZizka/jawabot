
package org.jboss.jawabot.web._pg;


import javax.inject.Inject;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.jboss.jawabot.plugin.irc.Channel;
import org.jboss.jawabot.plugin.irc.ChannelManager;
import org.jboss.jawabot.plugin.irc.web._co.ChannelDetailPanel;
import org.jboss.jawabot.plugin.irc.web._co.ChannelListPanel;
import org.jboss.jawabot.web._base.BaseLayoutPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  No idea what this page is for. Dump?
 *  @author Ondrej Zizka
 */
public class ChannelPage extends BaseLayoutPage
{
  private static final Logger log = LoggerFactory.getLogger( ChannelPage.class );

  @Inject private ChannelManager channelManager;
  
  
  // Page params.
  public final static String PARAM_NAME = "name";
  
  
  /** Page const */
  public ChannelPage( PageParameters params ) {
     super(params);
     log.debug(" Page params: " + params );

     String name = params.getString(PARAM_NAME);
     Channel chan = channelManager.byName( name );
 
     if( chan != null ){
        add( new Label("heading", "Channel "+chan.getName()+" - details"));
        add( new ChannelDetailPanel( "channelPanel", chan ) );
     }
     else{
        add( new Label("heading", "List of channels"));
        add( new ChannelListPanel("channelPanel") );
     }
  }


}// class HomePage
