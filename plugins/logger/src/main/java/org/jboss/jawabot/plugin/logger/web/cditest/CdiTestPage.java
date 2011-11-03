package org.jboss.jawabot.plugin.logger.web.cditest;

import java.io.Serializable;
import javax.inject.Inject;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.jboss.jawabot.plugin.irc.Channel;
import org.jboss.jawabot.plugin.irc.ChannelManager;
import org.jboss.jawabot.plugin.logger.web._co.ChannelLogPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: Test if CDI injects and handles interceptors in beans injected to this page's beans.
 * 
 * @author Ondrej Zizka
 */
public class CdiTestPage extends WebPage implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(CdiTestPage.class);
    
    @Inject
    private ChannelManager channelManager;

    
    
    /** Page const */
    public CdiTestPage(PageParameters params) {
        super(params);

        String name = "#jbossas";
        Channel chan = channelManager.byName(name);

        // Log table or channel list.
        log.debug("Using ChannelLogPanel with channel: " + chan);
        add(new Label("heading", "Channel " + chan.getName() + " - log"));
        add(new ChannelLogPanel("channelLogPanel", chan));
    }

    //<editor-fold defaultstate="collapsed" desc="get/set">
    //</editor-fold>
}// class HomePage
