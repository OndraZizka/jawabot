package org.jboss.jawabot.irc;

import cz.dynawest.util.plugin.IPluginLifeCycle;
import org.jboss.jawabot.ex.JawaBotException;
import org.jboss.jawabot.irc.ent.IrcEvMessage;

/**
 *  
 *  @author Ondrej Zizka
 */
public interface IIrcPluginHook <TInitObject extends Object> extends IPluginLifeCycle <TInitObject, JawaBotException> {
   
   public void onMessage( IrcEvMessage message, IrcBotProxy bot ) throws IrcPluginException;
   
   public void onPrivateMessage( IrcEvMessage message, IrcBotProxy bot ) throws IrcPluginException;
   
   public void onJoin( String channel, String user, IrcBotProxy bot );
   
   public void onPart( String channel, String user, IrcBotProxy bot );
   
   public void onBotJoinChannel( String channel, IrcBotProxy bot );

   public void onConnect( IrcBotProxy pircBotProxy );
   
   public void onDisconnect( IrcBotProxy pircBotProxy );
   
}// class

