package org.jboss.jawabot.plugin.autoop.irc;

import org.jboss.jawabot.irc.IIrcPluginHook;
import org.jboss.jawabot.irc.IrcBotProxy;
import org.jboss.jawabot.irc.IrcPluginHookBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Automatically ops everyone in the channel.
 *  Also asks for operator if not having it.
 * 
 *  @author Ondrej Zizka
 */
public class AutoOpIrcPluginHook extends IrcPluginHookBase implements IIrcPluginHook<Object> {
   private static final Logger log = LoggerFactory.getLogger( AutoOpIrcPluginHook.class );

   
   // IRC stuff.


    @Override
    public void onJoin( String channel, String user, IrcBotProxy bot  ) {
        if( this.isCanHaveOperator( channel, user ) )
            bot.op( channel, user );
    }


    /**
     *  TODO: Perhaps get this info from database?
     */
    private boolean isCanHaveOperator( String channel, String user ) {
        return true;
    }
   
   
   
   
   
   

   
}// class

