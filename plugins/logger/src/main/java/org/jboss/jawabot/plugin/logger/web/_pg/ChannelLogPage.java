package org.jboss.jawabot.plugin.logger.web._pg;

import java.io.Serializable;
import javax.inject.Inject;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.jboss.jawabot.plugin.irc.Channel;
import org.jboss.jawabot.plugin.irc.ChannelManager;
import org.jboss.jawabot.plugin.logger.web._co.ChannelLogPanel;
import org.jboss.jawabot.plugin.logger.web._co.LoggedChannelsListPanel;
import org.jboss.jawabot.web._base.BaseLayoutPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Ondrej Zizka
 */
public class ChannelLogPage extends BaseLayoutPage implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(ChannelLogPage.class);
    
    @Inject
    private ChannelManager channelManager;
    
    // Page params.
    public final static String PARAM_NAME = "name";
    
    // Page state.
    private boolean onlyShowMessages = false;

    
    
    /** Page const */
    public ChannelLogPage(PageParameters params) {
        super(params);
        log.debug(" Page params: " + params);

        String name = params.getString(PARAM_NAME);
        Channel chan = name == null ? null : channelManager.byName(name);

        // Checkbox.
        add(new AjaxCheckBox("onlyMessages", new PropertyModel<Boolean>(this, "onlyShowMessages")) {
            protected void onUpdate(AjaxRequestTarget target) {
                target.appendJavascript(" document.getElementById('channelLogPanel').className = 'onlyMessages';");
            }
        });


        // Log table or channel list.
        if (chan == null) {
            log.debug("Channel is null, using ChannelListPanel.");
            add(new Label("heading", "Logged Channels"));
            add(new LoggedChannelsListPanel("channelLogPanel"));
        } else {
            log.debug("Using ChannelLogPanel with channel: " + chan);
            add(new Label("heading", "Channel " + chan.getName() + " - log"));
            add(new ChannelLogPanel("channelLogPanel", chan));
        }
    }

    /*
    @Override
    protected void onDetach() {
        this.channelManager = null;
        super.onDetach();
    }*/
    
    
    
    //<editor-fold defaultstate="collapsed" desc="get/set">
    public boolean isOnlyShowMessages() {
        return onlyShowMessages;
    }

    public void setOnlyShowMessages(boolean onlyShowMessages) {
        this.onlyShowMessages = onlyShowMessages;
    }
    //</editor-fold>
}// class HomePage
