
package org.jboss.jawabot;

import org.jboss.jawabot.config.beans.ConfigBean;
import org.jboss.jawabot.ex.JawaBotException;

/**
 *
 * @author ondra
 */
public interface IModuleHook {
   
   public void initModule( JawaBot jawaBot, ConfigBean configBean ) throws JawaBotException;
   
   public void startModule() throws JawaBotException;
   
   public void stopModule() throws JawaBotException;
   
   public void applyConfig( ConfigBean configBean ) throws JawaBotException;
   
   public void mergeConfig( ConfigBean configBean ) throws JawaBotException;
   
   public void destroyModule() throws JawaBotException;
   
}
