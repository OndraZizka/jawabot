/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.jawabot.plugin.logger.irc;

import java.util.List;
import org.jboss.jawabot.irc.ent.IrcEvMessage;

/**
 *
 * @author Ondrej Zizka
 */
public interface ILoggerService {

   List<IrcEvMessage> getMessages(MessagesCriteria msgCriteria);

   void storeMessage(IrcEvMessage msg);
   
}// interface

