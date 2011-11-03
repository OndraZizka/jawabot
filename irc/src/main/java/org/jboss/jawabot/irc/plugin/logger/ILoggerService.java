/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.jawabot.irc.plugin.logger;

import java.util.List;
import org.jboss.jawabot.irc.model.IrcMessage;

/**
 *
 * @author Ondrej Zizka
 */
public interface ILoggerService {

   List<IrcMessage> getMessages(MessagesCriteria msgCriteria);

   void storeMessage(IrcMessage msg);
   
}// interface

