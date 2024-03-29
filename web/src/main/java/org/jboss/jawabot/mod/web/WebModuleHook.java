
package org.jboss.jawabot.mod.web;

import org.jboss.jawabot.IModuleHook;
import org.jboss.jawabot.JawaBot;
import org.jboss.jawabot.config.beans.ConfigBean;
import org.jboss.jawabot.ex.JawaBotException;
import org.jboss.jawabot.web.RunInJetty;

/**
 *
 * @author Ondrej Zizka
 */
public class WebModuleHook implements IModuleHook {

    @Override
    public void initModule( Object initObject ) throws Exception {
        RunInJetty.run();
    }
    
    @Override
    public void initModule( Object jawaBot_, ConfigBean configBean ) throws JawaBotException {
        JawaBot jawaBot = (JawaBot) jawaBot_;
        RunInJetty.run();
    }

    @Override
    public void startModule() throws JawaBotException {
    }

    @Override
    public void stopModule() throws JawaBotException {
    }

    @Override
    public void applyConfig( ConfigBean configBean ) throws JawaBotException {
    }

    @Override
    public void mergeConfig( ConfigBean configBean ) throws JawaBotException {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public void destroyModule() throws JawaBotException {
    }

}
