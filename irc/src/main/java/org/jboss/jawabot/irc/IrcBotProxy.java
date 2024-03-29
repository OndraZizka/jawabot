package org.jboss.jawabot.irc;

import java.util.Arrays;
import java.util.List;
import org.jboss.jawabot.irc.ent.IrcEvent;
import org.jibble.pircbot.beans.User;

/**
 *  Limits possible calls to a set safe to provide to plugins.
 *  Delegates calls to PircBot.
 * 
 *  Note: I had to specify the class to JawaIrcBot instead of PircBot 
 *  to overcome some PircBot's limitations like that onChannelInfo() calls 
 *  callback method directly, which would prevent plugins differentiate 
 *  who asked for channel info.
 * 
 *  TODO: This should really be just a proxy, we should extend IrcConnection and put most methods there instead.
 *  
 *  @author Ondrej Zizka
 */
public class IrcBotProxy {

    private JawaIrcBot jawaIrcBot;

    public IrcBotProxy( JawaIrcBot jawaIrcBot ) {
        this.jawaIrcBot = jawaIrcBot;
    }

    
    // ==== Sending. ==== 
    
    public final void sendMessage(String target, String message) {
        jawaIrcBot.getConn().sendMessage(target, message);
    }

    public final void sendMessage(String user, String channel, String message) {
        if (channel == null) {
            if (user == null) {
                throw new IllegalArgumentException("Neither user nor channel set, can't send the message: " + message);
            } else {
                this.jawaIrcBot.getConn().sendMessage(user, message);
            }
        } else {
            String whoFor = (user == null) ? "" : (user + ": ");
            this.jawaIrcBot.getConn().sendMessage(channel, whoFor + message);
        }
    }
    
    /**
     *  Sends given message to event's user and channel.
     *  Equivalent to <code>this.sendMessage( evt.getUser(), evt.getChannel(), text );<code>
     */
    public void sendReplyTo( IrcEvent evt, String text ){
        this.sendMessage( evt.getUser(), evt.getChannel(), text );
    }

    public final void sendNotice(String target, String notice) {
        jawaIrcBot.getConn().sendNotice(target, notice);
    }

    public final void sendInvite(String nick, String channel) {
        jawaIrcBot.getConn().sendInvite(nick, channel);
    }

    public final void sendCTCPCommand(String target, String command) {
        jawaIrcBot.getConn().sendCTCPCommand(target, command);
    }

    public final void sendAction(String target, String action) {
        jawaIrcBot.getConn().sendAction(target, action);
    }

    
    // ==== Users related. ==== 
    
    public final void op(String channel, String nick) {
        jawaIrcBot.getConn().op(channel, nick);
    }

    public final void deOp(String channel, String nick) {
        jawaIrcBot.getConn().deOp(channel, nick);
    }

    public final void kick(String channel, String nick, String reason) {
        jawaIrcBot.getConn().kick(channel, nick, reason);
    }

    public final void kick(String channel, String nick) {
        jawaIrcBot.getConn().kick(channel, nick);
    }

    public final void voice(String channel, String nick) {
        jawaIrcBot.getConn().voice(channel, nick);
    }

    public final void deVoice(String channel, String nick) {
        jawaIrcBot.getConn().deVoice(channel, nick);
    }

    public final void ban(String channel, String hostmask) {
        jawaIrcBot.getConn().ban(channel, hostmask);
    }

    public final void unBan(String channel, String hostmask) {
        jawaIrcBot.getConn().unBan(channel, hostmask);
    }

    
    // ==== Channel related. ==== 
    
    public final List<User> getUsers(String channel) {
        return Arrays.asList( jawaIrcBot.getConn().getUsers(channel) );
    }
    

    public final void setTopic(String channel, String topic) {
        jawaIrcBot.getConn().setTopic(channel, topic);
    }

    public final void setMode(String channel, String mode) {
        jawaIrcBot.getConn().setMode(channel, mode);
    }

    public final void partChannel(String channel, String reason) {
        jawaIrcBot.getConn().partChannel(channel, reason);
    }

    public final void partChannel(String channel) {
        jawaIrcBot.getConn().partChannel(channel);
    }

    public final void joinChannel(String channel, String key) {
        jawaIrcBot.getConn().joinChannel(channel, key);
    }

    public final void joinChannel(String channel) {
        jawaIrcBot.getConn().joinChannel(channel);
    }

    
    //  ==== Bot- and connection-related. ====
    
    public final String[] getChannels() {
        return jawaIrcBot.getConn().getChannels();
    }
    
    public Boolean isUserInChannel( String nick, String channel ){
        return jawaIrcBot.getConn().isUserInChannel( nick, channel );
    }
    
    public Boolean isUserInChannel( String nick, String channel, boolean normalize ){
        if( ! normalize )
            return jawaIrcBot.getConn().isUserInChannel( nick, channel );
        else
            return jawaIrcBot.isUserInChannel( nick, channel, true );
    }
    
    public boolean isUserInChannelSafe( String nick, String channel, boolean normalize ){
        Boolean isIn = this.isUserInChannel( nick, channel, normalize );
        if( null == isIn )  return false;
        return isIn;
    }
    
    public final void listChannels( ChannelInfoHandler channelInfoHandler ) {
        jawaIrcBot.listChannels( channelInfoHandler );
    }

    public void listUsersInChannel(String channel, UserListHandler handler) {
        jawaIrcBot.listUsersInChannel(channel, handler);
    }

    

    /*public final void listChannels(String parameters) {
        jawaIrcBot.listChannels(parameters);
    }

    public final void listChannels() {
        jawaIrcBot.listChannels();
    }*/

    public final String getVersion() {
        return jawaIrcBot.getConn().getVersion();
    }

    public final String getServer() {
        return jawaIrcBot.getConn().getServer();
    }

    public final int getPort() {
        return jawaIrcBot.getConn().getPort();
    }

    public String getNick() {
        return jawaIrcBot.getConn().getNick();
    }

    public final String getName() {
        return jawaIrcBot.getConn().getName();
    }

    public final String getLogin() {
        return jawaIrcBot.getConn().getLogin();
    }


    public void sendDebugMessage( String msg ) {
        jawaIrcBot.sendDebugMessage( msg );
    }

}// class

