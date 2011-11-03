package org.jboss.jawabot.irc;

import org.jboss.jawabot.ex.JawaBotException;

/**
 *  
 *  @author Ondrej Zizka
 */
public class IrcPluginException extends JawaBotException {

   public IrcPluginException() {
   }

   public IrcPluginException(String message) {
      super(message);
   }

   public IrcPluginException(String message, Throwable cause) {
      super(message, cause);
   }

   public IrcPluginException(Throwable cause) {
      super(cause);
   }
   
}// class

