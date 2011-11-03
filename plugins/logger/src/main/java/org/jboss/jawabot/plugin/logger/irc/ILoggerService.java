/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.jawabot.plugin.logger.irc;

import java.util.List;
import org.jboss.jawabot.irc.ent.IrcMessage;

/**
 *
 * @author Ondrej Zizka
 */
public interface ILoggerService {

   List<IrcMessage> getMessages(MessagesCriteria msgCriteria);

   void storeMessage(IrcMessage msg);
   
}// interface

