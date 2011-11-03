package org.jboss.jawabot.irc;

import org.jibble.pircbot.User;

/**
 *  
 *  @author Ondrej Zizka
 */
public interface UserListHandler {
    
    public void onUserList(String channel, User[] users);
    
    public boolean isDisconnectFlag();
    public void setDisconnectFlag( boolean b );
    
    
}// class

