package org.jboss.jawabot.plugin.logger.web._pg;

import java.io.Serializable;
import java.util.Date;
import javax.inject.Inject;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.datetime.DateConverter;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.jboss.jawabot.plugin.irc.Channel;
import org.jboss.jawabot.plugin.irc.ChannelManager;
import org.jboss.jawabot.plugin.logger.web._co.ChannelLogLinkSimplePanel;
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
    private boolean hideJoinsParts = false;
    
    // Components.
    private Form navigForm;
    private ChannelLogPanel logPanel;

    // Utils.
    final DateConverter dcDate = new PatternDateConverter("yyyy-MM-dd", false);
    
    
    
    /** Page const */
    public ChannelLogPage( PageParameters params ) {
        super(params);
        log.debug(" Page params: " + params);

        // Channel name
        String name = params.getString(PARAM_NAME);
        name = ChannelLogLinkSimplePanel.unescapeHashes(name);
        Channel chan = name == null ? null : channelManager.byName(name);
        
        
        // Log table or channel list?
        if (chan == null) {
            log.debug("Channel is null, redirecting to LoggerChannelsListPage.");
            setResponsePage( LoggerChannelsListPage.class );
            return;
        }
        
        // Channel list
        log.debug("Using ChannelLogPanel with channel: " + chan);
        add(new Label("heading", "Channel " + chan.getName() + " - log"));
        this.logPanel = new ChannelLogPanel("channelLogPanel", chan);
        this.logPanel.setOutputMarkupId(true);
        add( logPanel );
        
        
        // Form;
        this.navigForm = new Form("navig");
        add(navigForm);

        // Since, Until, User
        this.navigForm.add( new DateTextField("since", new PropertyModel<Date>(logPanel,"crit.since"), this.dcDate) );
        this.navigForm.add( new DateTextField("until", new PropertyModel<Date>(logPanel,"crit.until"), this.dcDate) );
        this.navigForm.add( new TextField("user", new PropertyModel<Date>(logPanel,"crit.user")) );
        
        // Checkbox - joins / parts.
        this.navigForm.add(new AjaxCheckBox("hideJoinsParts", new PropertyModel<Boolean>(this, "hideJoinsParts")) {
            protected void onUpdate(AjaxRequestTarget target) {
                //target.appendJavascript("document.getElementById('channelLogPanel').className = 'onlyMessages';");
                target.appendJavascript("$('#eventLog').toggleClass('hideJoinsParts');");
            }
        });
        
        // Buttons.
        this.navigForm.add( new AjaxFallbackButton("show", this.navigForm) {
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.addComponent( navigForm );
                target.addComponent( logPanel );
            }
        });

        this.navigForm.add( new AjaxFallbackButton("prev", this.navigForm) {
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.addComponent( navigForm );
                target.addComponent( logPanel );
                logPanel.getCrit().adjustSinceByDays(-1);
                logPanel.getCrit().adjustUntilByDays(-1);
            }
        });

        this.navigForm.add( new AjaxFallbackButton("next", this.navigForm) {
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.addComponent( navigForm );
                target.addComponent( logPanel );
                logPanel.getCrit().adjustSinceByDays(+1);
                logPanel.getCrit().adjustUntilByDays(+1);
            }
        });

        this.navigForm.add( new AjaxFallbackButton("otherChannel", this.navigForm) {
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                setResponsePage( LoggerChannelsListPage.class );
            }
        });

    }

    /*
    @Override
    protected void onDetach() {
        this.channelManager = null;
        super.onDetach();
    }*/
    
    
    
    /**
     *  Options what to show and what to hide.
     *  Complement to IrcEventsCriteria which filters what's loaded.
     */
    class ViewOptions {
        public boolean hideJoinsParts = false;
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="get/set">
    public boolean isHideJoinsParts() {
        return hideJoinsParts;
    }

    public void setHideJoinsParts(boolean hideJoinsParts) {
        this.hideJoinsParts = hideJoinsParts;
    }
    //</editor-fold>

}// class HomePage
