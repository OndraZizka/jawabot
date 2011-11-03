package org.jboss.jawabot.plugin.autoop.irc;

import org.jboss.jawabot.irc.IIrcPluginHook;
import org.jboss.jawabot.irc.IrcBotProxy;
import org.jboss.jawabot.irc.IrcPluginHookBase;
import org.jboss.jawabot.irc.ent.IrcEvJoin;
import org.jibble.pircbot.User;
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
    public void onJoin( IrcEvJoin event, IrcBotProxy bot ) {
        if( ! this.isCanHaveOperator( event.getChannel(), event.getUser() ) )
            return;
        bot.op( event.getChannel(), event.getUser() );
    }


    @Override
    public void onBotJoinChannel( String channel, IrcBotProxy bot ) {
        
        User[] users = bot.getUsers( channel );
        for( User user : users ) {
            if( user.isOp() )
                continue;
            if( ! this.isCanHaveOperator( channel, user.getNick() ) )
                continue;
            bot.op( channel, user.getNick() );
        }
        
    }
    
    


    /**
     *  TODO: Perhaps get this info from database?
     */
    private boolean isCanHaveOperator( String channel, String user ) {
        return true;
    }
   
   
   
   
   
   

   
}// class

