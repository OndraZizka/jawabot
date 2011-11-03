package org.jboss.jawabot.irc;

import cz.dynawest.util.plugin.IPluginLifeCycle;
import org.jboss.jawabot.ex.JawaBotException;
import org.jboss.jawabot.irc.ent.IrcEvAction;
import org.jboss.jawabot.irc.ent.IrcEvJoin;
import org.jboss.jawabot.irc.ent.IrcEvMessage;
import org.jboss.jawabot.irc.ent.IrcEvNickChange;
import org.jboss.jawabot.irc.ent.IrcEvPart;

/**
 *  
 *  @author Ondrej Zizka
 */
public interface IIrcPluginHook <TInitObject extends Object> extends IPluginLifeCycle <TInitObject, JawaBotException> {
   
   public void onMessage( IrcEvMessage message, IrcBotProxy bot ) throws IrcPluginException;
   
   public void onPrivateMessage( IrcEvMessage message, IrcBotProxy bot ) throws IrcPluginException;
   
   public void onJoin( IrcEvJoin ev, IrcBotProxy bot );
   
   public void onPart( IrcEvPart ev, IrcBotProxy bot );
   
   public void onAction( IrcEvAction ev, IrcBotProxy bot );
   
   public void onBotJoinChannel( String channel, IrcBotProxy bot );

   public void onConnect( IrcBotProxy pircBotProxy );
   
   public void onDisconnect( IrcBotProxy pircBotProxy );

    public void onNickChange(IrcEvNickChange ev, IrcBotProxy pircBotProxy);
   
}// class

