package org.jboss.jawabot.plugin.logger.web;

import org.apache.wicket.request.target.coding.MixedParamHybridUrlCodingStrategy;
import org.jboss.jawabot.mod.web.IPageMount;
import org.jboss.jawabot.mod.web.MountProxy;
import org.jboss.jawabot.plugin.logger.web._pg.ChannelLogPage;

/**
 *  Mounts Logger pages into Wicket.
 *  @author Ondrej Zizka
 */
public class LoggerPageMount implements IPageMount {

    @Override
    public void mount( MountProxy wicketApp ) {
        wicketApp.mount( new MixedParamHybridUrlCodingStrategy(
                "channelLog", ChannelLogPage.class, new String[]{"name"} // remaining are perhaps "since","until".
        ));
    }
    
}// class

