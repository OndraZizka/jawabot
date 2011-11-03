
package org.jboss.jawabot;

import cz.dynawest.util.plugin.IPluginLifeCycle;
import org.jboss.jawabot.config.beans.ConfigBean;

/**
 *
 * @author Ondrej Zizka
 */
public interface IModuleHook<TInit, TEx extends Exception> extends IPluginLifeCycle<TInit, TEx> {
   
   public void initModule( TInit jawaBot, ConfigBean configBean ) throws TEx;
   
   public void applyConfig( ConfigBean configBean ) throws TEx;
   
   public void mergeConfig( ConfigBean configBean ) throws TEx;
   
}
