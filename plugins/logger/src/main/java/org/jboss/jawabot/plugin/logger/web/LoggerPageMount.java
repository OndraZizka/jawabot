package org.jboss.jawabot.plugin.logger.web;

import cz.dynawest.wicket.NonVersionedMixedParamHybridUrlCodingStrategy;
import org.jboss.jawabot.mod.web.IPageMount;
import org.jboss.jawabot.mod.web.MountProxy;
import org.jboss.jawabot.plugin.logger.web._pg.ChannelLogPage;
import org.jboss.jawabot.plugin.logger.web._pg.LoggerChannelsListPage;

/**
 *  Mounts Logger pages into Wicket.
 *  @author Ondrej Zizka
 */
public class LoggerPageMount implements IPageMount {

    @Override
    public void mount( MountProxy wicketApp ) {
        wicketApp.mount( new NonVersionedMixedParamHybridUrlCodingStrategy(
                "channelLog", ChannelLogPage.class, new String[]{"name"} // remaining are perhaps "since","until".
        ));
        wicketApp.mount( new NonVersionedMixedParamHybridUrlCodingStrategy(
                "logs", LoggerChannelsListPage.class, new String[]{}
        ));
    }
    
}// class

