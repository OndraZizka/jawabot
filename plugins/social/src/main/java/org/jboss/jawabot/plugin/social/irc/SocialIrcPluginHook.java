package org.jboss.jawabot.plugin.social.irc;

import java.util.Random;
import org.jboss.jawabot.irc.IIrcPluginHook;
import org.jboss.jawabot.irc.IrcBotProxy;
import org.jboss.jawabot.irc.IrcPluginException;
import org.jboss.jawabot.irc.IrcPluginHookBase;
import org.jboss.jawabot.irc.ent.IrcEvMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  Social - reacts on various social protocol tokens like "thanks" etc (For Rado Husar :) 
 * 
 *  @author Ondrej Zizka
 *  TODO:  Maybe defer this to some rules engine or chatting bot core.
 */
public class SocialIrcPluginHook extends IrcPluginHookBase implements IIrcPluginHook<Object> {
    private static final Logger log = LoggerFactory.getLogger( SocialIrcPluginHook.class );



    @Override
    public void onMessage( IrcEvMessage message, IrcBotProxy bot ) throws IrcPluginException {

        //  Is it for us?
        if( ! bot.getNick().equals( message.getRecipient() ) ) 
            return;
        
        String msgNorm = message.getPayload().toLowerCase();
        String reply = null;
        
        // Thanks.
        if(        msgNorm.contains("thanks")
                || msgNorm.contains("thank you")
        ){
            reply = new String[]{"You're welcome", "welcome", "HTH", "yw"}[new Random().nextInt(4)];
        }
        
        // Hi, hello and such.
        if(        msgNorm.startsWith("hi")
                || msgNorm.startsWith("hello")
                || msgNorm.startsWith("ping")
        ){
            reply = new String[]{"hi", "hello", "hey"}[new Random().nextInt(3)];
        }
        
        // Bye.
        if(        msgNorm.contains("bye")
                || msgNorm.contains("see you")
        ){
            reply = new String[]{"bye", "see you"}[new Random().nextInt(3)];
        }
        
        
        // Send it.
        if( null != reply )
            bot.sendReplyTo( message, reply );
        
    }// onMessage()
    

}// class

