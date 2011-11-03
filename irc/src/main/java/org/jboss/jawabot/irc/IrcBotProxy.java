package org.jboss.jawabot.irc;

import org.jboss.jawabot.irc.ent.IrcEvent;
import org.jibble.pircbot.User;

/**
 *  Limits possible calls to a set safe to provide to plugins.
 *  Delegates calls to PircBot.
 * 
 *  Note: I had to specify the class to JawaIrcBot instead of PircBot 
 *  to overcome some PircBot's limitations like that onChannelInfo() calls 
 *  callback method directly, which would prevent plugins differentiate 
 *  who asked for channel info.
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
        jawaIrcBot.sendMessage(target, message);
    }

    public final void sendMessage(String user, String channel, String message) {
        if (channel == null) {
            if (user == null) {
                throw new IllegalArgumentException("Neither user nor channel set, can't send the message: " + message);
            } else {
                this.jawaIrcBot.sendMessage(user, message);
            }
        } else {
            String whoFor = (user == null) ? "" : (user + ": ");
            this.jawaIrcBot.sendMessage(channel, whoFor + message);
        }
    }
    
    public void sendReplyTo( IrcEvent evt, String text ){
        this.sendMessage( evt.getUser(), evt.getChannel(), text );
    }

    public final void sendNotice(String target, String notice) {
        jawaIrcBot.sendNotice(target, notice);
    }

    public final void sendInvite(String nick, String channel) {
        jawaIrcBot.sendInvite(nick, channel);
    }

    public final void sendCTCPCommand(String target, String command) {
        jawaIrcBot.sendCTCPCommand(target, command);
    }

    public final void sendAction(String target, String action) {
        jawaIrcBot.sendAction(target, action);
    }

    
    // ==== Users related. ==== 
    
    public final void op(String channel, String nick) {
        jawaIrcBot.op(channel, nick);
    }

    public final void deOp(String channel, String nick) {
        jawaIrcBot.deOp(channel, nick);
    }

    public final void kick(String channel, String nick, String reason) {
        jawaIrcBot.kick(channel, nick, reason);
    }

    public final void kick(String channel, String nick) {
        jawaIrcBot.kick(channel, nick);
    }

    public final void voice(String channel, String nick) {
        jawaIrcBot.voice(channel, nick);
    }

    public final void deVoice(String channel, String nick) {
        jawaIrcBot.deVoice(channel, nick);
    }

    public final void ban(String channel, String hostmask) {
        jawaIrcBot.ban(channel, hostmask);
    }

    public final void unBan(String channel, String hostmask) {
        jawaIrcBot.unBan(channel, hostmask);
    }

    
    // ==== Channel related. ==== 
    
    public final User[] getUsers(String channel) {
        return jawaIrcBot.getUsers(channel);
    }
    
    public boolean isUserInChannel( String channel, String user ){
        return this.isUserInChannel(channel, user, false);
    }

    /**
     * @param normalize  Whether ozizka-pto matches with ozizka_lunch. Normalization basically removes any suffix after _, -, ~, | etc.
     * @see   IrcUtils.normalizeUserNick()
     */
    public boolean isUserInChannel( String channel, String nick, boolean normalize ){
        assert (nick != null);
        //if( ! normalize )
            //return Arrays.asList( this.getUsers(channel) ).contains( nick ); // String != User, and User.equals(String) doesn't apply here.

        if( normalize )
            nick = IrcUtils.normalizeUserNick(nick);
        for( User user : this.getUsers(channel) ){
            if( IrcUtils.normalizeUserNick( user.getNick() ).equals( nick ) )
                return true;
        }
        return false;
            
    }

    public final void setTopic(String channel, String topic) {
        jawaIrcBot.setTopic(channel, topic);
    }

    public final void setMode(String channel, String mode) {
        jawaIrcBot.setMode(channel, mode);
    }

    public final void partChannel(String channel, String reason) {
        jawaIrcBot.partChannel(channel, reason);
    }

    public final void partChannel(String channel) {
        jawaIrcBot.partChannel(channel);
    }

    public final void joinChannel(String channel, String key) {
        jawaIrcBot.joinChannel(channel, key);
    }

    public final void joinChannel(String channel) {
        jawaIrcBot.joinChannel(channel);
    }

    
    //  ==== Bot- and connection-related. ====
    
    public final String[] getChannels() {
        return jawaIrcBot.getChannels();
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
        return jawaIrcBot.getVersion();
    }

    public final String getServer() {
        return jawaIrcBot.getServer();
    }

    public final int getPort() {
        return jawaIrcBot.getPort();
    }

    public String getNick() {
        return jawaIrcBot.getNick();
    }

    public final String getName() {
        return jawaIrcBot.getName();
    }

    public final String getLogin() {
        return jawaIrcBot.getLogin();
    }


    public void sendDebugMessage( String msg ) {
        jawaIrcBot.sendDebugMessage( msg );
    }

}// class

