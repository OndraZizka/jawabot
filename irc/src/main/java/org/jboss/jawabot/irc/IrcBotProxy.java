package org.jboss.jawabot.irc;

import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

/**
 *  Limits possible calls to a set safe to provide to plugins.
 *  Delegates calls to PircBot.
 *  
 *  @author Ondrej Zizka
 */
public class IrcBotProxy {
   
   private PircBot pircbot;

   public IrcBotProxy(PircBot pircbot) {
      this.pircbot = pircbot;
   }
   
   
   
   // Sending.
   
   public final void sendMessage(String target, String message) {
      pircbot.sendMessage(target, message);
   }

   public final void sendNotice(String target, String notice) {
      pircbot.sendNotice(target, notice);
   }

   public final void sendInvite(String nick, String channel) {
      pircbot.sendInvite(nick, channel);
   }

   public final void sendCTCPCommand(String target, String command) {
      pircbot.sendCTCPCommand(target, command);
   }

   public final void sendAction(String target, String action) {
      pircbot.sendAction(target, action);
   }


   // Users related.
   
   public final void op(String channel, String nick) {
      pircbot.op(channel, nick);
   }

   public final void deOp(String channel, String nick) {
      pircbot.deOp(channel, nick);
   }

   public final void kick(String channel, String nick, String reason) {
      pircbot.kick(channel, nick, reason);
   }

   public final void kick(String channel, String nick) {
      pircbot.kick(channel, nick);
   }

   public final void voice(String channel, String nick) {
      pircbot.voice(channel, nick);
   }

   public final void deVoice(String channel, String nick) {
      pircbot.deVoice(channel, nick);
   }

   public final void ban(String channel, String hostmask) {
      pircbot.ban(channel, hostmask);
   }

   public final void unBan(String channel, String hostmask) {
      pircbot.unBan(channel, hostmask);
   }



   // Channel related.
   
   public final User[] getUsers(String channel) {
      return pircbot.getUsers(channel);
   }

   public final void setTopic(String channel, String topic) {
      pircbot.setTopic(channel, topic);
   }

   public final void setMode(String channel, String mode) {
      pircbot.setMode(channel, mode);
   }

   public final void partChannel(String channel, String reason) {
      pircbot.partChannel(channel, reason);
   }

   public final void partChannel(String channel) {
      pircbot.partChannel(channel);
   }

   public final void joinChannel(String channel, String key) {
      pircbot.joinChannel(channel, key);
   }

   public final void joinChannel(String channel) {
      pircbot.joinChannel(channel);
   }

   
   //  Bot- and connection-related.
   
   public final String[] getChannels() {
      return pircbot.getChannels();
   }
   
   public final void listChannels(String parameters) {
      pircbot.listChannels(parameters);
   }

   public final void listChannels() {
      pircbot.listChannels();
   }

   
   public final String getVersion() {
      return pircbot.getVersion();
   }

   public final String getServer() {
      return pircbot.getServer();
   }

   public final int getPort() {
      return pircbot.getPort();
   }

   public String getNick() {
      return pircbot.getNick();
   }

   public final String getName() {
      return pircbot.getName();
   }

   public final String getLogin() {
      return pircbot.getLogin();
   }

   
   
   
}// class

