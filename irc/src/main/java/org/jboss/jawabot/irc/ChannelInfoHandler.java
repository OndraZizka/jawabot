package org.jboss.jawabot.irc;

/**
 *  
 *  @author Ondrej Zizka
 */
public interface ChannelInfoHandler {
   
   public void onChannelInfo( String channel, int userCount, String topic );
   
}// class

