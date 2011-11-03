package org.jboss.jawabot.plugin.jira.irc;

import org.jboss.jawabot.irc.IIrcPluginHook;
import org.jboss.jawabot.irc.IrcBotProxy;
import org.jboss.jawabot.irc.IrcPluginException;
import org.jboss.jawabot.irc.IrcPluginHookBase;
import org.jboss.jawabot.irc.model.IrcMessage;
import org.jibble.pircbot.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Former JiraBot.
 *  Scans messages for JIRA IDs and when spots one, prints basic info about it.
 * 
 *  @author Ondrej Zizka
 */
public class JiraIrcPluginHook extends IrcPluginHookBase implements IIrcPluginHook<Object> {
    private static final Logger log = LoggerFactory.getLogger( JiraIrcPluginHook.class );

   
    // IRC stuff.


    @Override
    public void onMessage( IrcMessage message, IrcBotProxy bot ) throws IrcPluginException {
        // TODO
    }

   
   
   
   

   
}// class

