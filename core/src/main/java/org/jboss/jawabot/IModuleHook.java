
package org.jboss.jawabot;

import cz.dynawest.util.plugin.IPluginLifeCycle;
import org.jboss.jawabot.config.beans.ConfigBean;
import org.jboss.jawabot.ex.JawaBotException;

/**
 *
 * @author Ondrej Zizka
 */
public interface IModuleHook<T> extends IPluginLifeCycle<T> {
   
   public void initModule( JawaBot jawaBot, ConfigBean configBean ) throws JawaBotException;
   
   public void applyConfig( ConfigBean configBean ) throws JawaBotException;
   
   public void mergeConfig( ConfigBean configBean ) throws JawaBotException;
   
}
