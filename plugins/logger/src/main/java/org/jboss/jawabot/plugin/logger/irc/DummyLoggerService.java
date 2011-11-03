package org.jboss.jawabot.plugin.logger.irc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jboss.jawabot.irc.ent.IrcEvMessage;
import org.jboss.jawabot.irc.ent.IrcEvent;
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
   public void storeEvent(IrcEvent ev) {
      log.info("Event saved: " + ev);
   }
   
   @Override
   public void storeMessage( IrcEvMessage msg ){
      log.info("Message saved: " + msg);
   }
   
   
   @Override
   public List<IrcEvMessage> getMessages( IrcEventCriteria msgCriteria ){
      List<IrcEvMessage> list = new ArrayList();
      list.add( new IrcEvMessage("serverA", "userA", "channelA", "text1", new Date() ) );
      list.add( new IrcEvMessage("serverB", "userB", "channelB", "text2", new Date() ) );
      list.add( new IrcEvMessage("serverC", "userB", "channelA", "text3", new Date() ) );
      return list;
   }

    @Override
    public List<? extends IrcEvent> getEvents(IrcEventCriteria msgCriteria) {
        return getMessages(msgCriteria);
    }

}// class

