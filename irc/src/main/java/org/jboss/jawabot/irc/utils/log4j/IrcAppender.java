package org.jboss.jawabot.irc.utils.log4j;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.jboss.jawabot.irc.JawaIrcBot;

/**
 *  Log4j appender which sends the messages to a debug channel.
 * 
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
public class IrcAppender extends AppenderSkeleton {

     private JawaIrcBot ircClient;


     public IrcAppender( JawaIrcBot ircClient ) {
         super( true );
         this.ircClient = ircClient;
     }


     @Override protected void append( LoggingEvent event ) {
         ircClient.sendDebugMessage( event.getRenderedMessage() );
     }

     @Override public void close() { }

     @Override public boolean requiresLayout() {
         return false;
     }
     
}// class IrcAppender
