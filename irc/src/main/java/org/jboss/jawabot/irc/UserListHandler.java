package org.jboss.jawabot.irc;

import org.jibble.pircbot.beans.User;

/**
 *  
 *  @author Ondrej Zizka
 */
public interface UserListHandler {
    
    public void onUserList(String channel, User[] users);
    
    public boolean isDisconnectFlag();
    public void setDisconnectFlag( boolean b );
    
    
}// class

