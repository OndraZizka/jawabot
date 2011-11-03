
package org.jboss.jawabot.plugin.logger.web._pg;


import javax.inject.Inject;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.jboss.jawabot.plugin.irc.Channel;
import org.jboss.jawabot.plugin.irc.ChannelManager;
import org.jboss.jawabot.plugin.irc.web._co.ChannelListPanel;
import org.jboss.jawabot.plugin.logger.web._co.ChannelLogPanel;
import org.jboss.jawabot.web._base.BaseLayoutPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Ondrej Zizka
 */
public class ChannelLogPage extends BaseLayoutPage
{
  private static final Logger log = LoggerFactory.getLogger( ChannelLogPage.class );

  @Inject private ChannelManager channelManager;
  
  
  // Page params.
  public final static String PARAM_NAME = "name";
  
  
  /** Page const */
  public ChannelLogPage( PageParameters params ) {
     super(params);
     log.debug(" Page params: " + params );

     String name = params.getString(PARAM_NAME);
     Channel chan = name == null ? null : channelManager.byName( name );
 
     if( chan == null ){
        add( new Label("heading", "Logged Channels"));
        add( new ChannelListPanel("channelLogPanel") );
     }
     else{
        add( new Label("heading", "Channel "+chan.getName()+" - details"));
        add( new ChannelLogPanel( "channelLogPanel", chan ) );
     }
  }


}// class HomePage
