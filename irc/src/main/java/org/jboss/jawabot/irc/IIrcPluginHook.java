package org.jboss.jawabot.irc;

import cz.dynawest.util.plugin.IPluginLifeCycle;
import org.jboss.jawabot.ex.JawaBotException;
import org.jboss.jawabot.irc.model.IrcMessage;

/**
 *  
 *  @author Ondrej Zizka
 */
public interface IIrcPluginHook <TInitObject extends Object> extends IPluginLifeCycle <TInitObject, JawaBotException> {
   
   public void onMessage( IrcMessage message, IrcBotProxy bot ) throws IrcPluginException;
   
   public void onPrivateMessage( IrcMessage message, IrcBotProxy bot ) throws IrcPluginException;
   
   public void onJoin( String channel, String user, IrcBotProxy bot );
   
   public void onPart( String channel, String user, IrcBotProxy bot );
   
}// class

