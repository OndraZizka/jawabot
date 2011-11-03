package org.jboss.jawabot.irc.plugin.logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jboss.jawabot.irc.model.IrcMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Takes care of storing the messages, searching, looking them up etc.
 * 
 * @author Ondrej Zizka
 */
public class DummyLoggerService implements ILoggerService {
   private static final Logger log = LoggerFactory.getLogger( DummyLoggerService.class );
   
   
   @Override
   public void storeMessage( IrcMessage msg ){
      log.info("Message saved: " + msg);
   }
   
   
   @Override
   public List<IrcMessage> getMessages( MessagesCriteria msgCriteria ){
      List<IrcMessage> list = new ArrayList();
      list.add( new IrcMessage("serverA", "userA", "channelA", "text1", new Date() ) );
      list.add( new IrcMessage("serverB", "userB", "channelB", "text2", new Date() ) );
      list.add( new IrcMessage("serverC", "userB", "channelA", "text3", new Date() ) );
      return list;
   }
   
}// class

