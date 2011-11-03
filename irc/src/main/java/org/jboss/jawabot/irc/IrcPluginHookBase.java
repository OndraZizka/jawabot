package org.jboss.jawabot.irc;

import org.jboss.jawabot.ex.JawaBotException;
import org.jboss.jawabot.irc.ent.IrcEvMessage;

/**
 *  Base abstract class to relieve people from writing every method of the interface.
 * 
 *  @author Ondrej Zizka
 */
public abstract class IrcPluginHookBase implements IIrcPluginHook<Object> {

   
   // IRC stuff.
   
   @Override
   public void onMessage(IrcEvMessage message, IrcBotProxy bot) throws IrcPluginException {
   }

   @Override
   public void onPrivateMessage(IrcEvMessage message, IrcBotProxy bot) throws IrcPluginException {
   }

   @Override
   public void onJoin(String channel, String user, IrcBotProxy bot ) {
   }

   @Override
   public void onPart(String channel, String user, IrcBotProxy bot ) {
   }

   @Override
   public void onBotJoinChannel(String channel, IrcBotProxy bot) {
   }
   
    @Override
    public void onConnect( IrcBotProxy pircBotProxy ) {
    }

    @Override
    public void onDisconnect( IrcBotProxy pircBotProxy ) {
    }
   

   
   // Lifecycle.
   
   @Override
   public void initModule(Object initObject) throws JawaBotException {
   }

   @Override
   public void startModule() throws JawaBotException {
   }

   @Override
   public void stopModule() throws JawaBotException {
   }

   @Override
   public void destroyModule() throws JawaBotException {
   }
   
}// class

