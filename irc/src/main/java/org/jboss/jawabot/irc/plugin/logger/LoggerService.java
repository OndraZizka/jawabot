package org.jboss.jawabot.irc.plugin.logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jboss.jawabot.irc.model.IrcMessage;

/**
 * Takes care of storing the messages, searching, looking them up etc.
 * 
 * @author Ondrej Zizka
 */
public class LoggerService {
   
   public void storeMessage( IrcMessage msg ){
      
   }
   
   public List<IrcMessage> getMessages( MessagesCriteria msgCriteria ){
      List<IrcMessage> list = new ArrayList();
      list.add( new IrcMessage("server", "user", "channel", "text", new Date() ) );
      return list;
   }
   
}// class

