package org.jboss.jawabot.irc;

/**
 *  To keep user from implementing this hack. 
 * 
 *  @author Ondrej Zizka
 */
public abstract class UserListHandlerBase implements UserListHandler {
    
    protected boolean disconnectFlag = false;

    public boolean isDisconnectFlag() {
        return disconnectFlag;
    }

    public void setDisconnectFlag(boolean flag) {
        this.disconnectFlag = flag;
    }

    
}// class

