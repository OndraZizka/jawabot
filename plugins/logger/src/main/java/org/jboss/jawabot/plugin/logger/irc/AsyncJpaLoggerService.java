package org.jboss.jawabot.plugin.logger.irc;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.jboss.jawabot.irc.ent.IrcEvMessage;
import org.jboss.jawabot.irc.ent.IrcEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Takes care of storing the messages, searching, looking them up etc.
 * Also provides a list of packages which contain entities of this plugin.
 * 
 * storeMessage() behaves asynchronously - passes persisting to a queue processed by another thread.
 * 
 * @author Ondrej Zizka
 */
public class AsyncJpaLoggerService implements ILoggerService {
   private static final Logger log = LoggerFactory.getLogger( AsyncJpaLoggerService.class );
   
   @Inject JpaLoggerService loggerService;
   
   
   private final BlockingQueue<IrcEvent> pendingMessages = new LinkedBlockingDeque();
   
   private Thread processingThread;
   
   
   @PostConstruct
   private void startEventStoringThread()
   {
      this.processingThread = 
         new Thread( new Runnable() {
            public void run() {
               while( ! Thread.interrupted() ){
                  try {
                     IrcEvent ev = pendingMessages.take();
                     loggerService.storeEvent( ev );
                  } catch (InterruptedException ex) {
                     break;
                  }
               }
               log.debug("Messages processing thread interrupted, stopping.");
            }
         });
      log.info("Starting event processing thread...");
      this.processingThread.setName("Event processing of " + AsyncJpaLoggerService.class.getSimpleName() );
      this.processingThread.run();
   }
   
   @PreDestroy
   private void stopEventStoringThread(){
      log.info("Stopping event processing thread...");
      this.processingThread.interrupt();
   }
   
   

   @Override
   public void storeEvent(IrcEvent ev) {
      this.pendingMessages.add(ev);
   }

   @Deprecated
   @Override
   public void storeMessage(IrcEvMessage msg) {
      this.pendingMessages.add(msg);
   }

   public void setLoggingEnabledForChannel(String ch, boolean enabled) {
      loggerService.setLoggingEnabledForChannel(ch, enabled);
   }

   public boolean isLoggingEnabledForChannel(String ch) {
      return loggerService.isLoggingEnabledForChannel(ch);
   }

   public List<IrcEvMessage> getMessages(IrcEventCriteria msgCriteria) {
      return loggerService.getMessages(msgCriteria);
   }

    @Override
    public List<? extends IrcEvent> getEvents(IrcEventCriteria msgCriteria) {
        return getMessages(msgCriteria);
    }

}// class

