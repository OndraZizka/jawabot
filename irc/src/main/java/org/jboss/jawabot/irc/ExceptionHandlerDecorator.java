package org.jboss.jawabot.irc;


import org.jboss.jawabot.irc.ent.IrcEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Decorator to save me handling exceptions everywhere.
 *  Catches exceptions leaked by plugins.
 *  TODO: Filter repeated exceptions.
 * 
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
public abstract class ExceptionHandlerDecorator<T extends IrcEvent> {
    private static final Logger log = LoggerFactory.getLogger( ExceptionHandlerDecorator.class );

    public void  handle( /*IIrcPluginHook plugin,*/ T evt, IrcBotProxy pircBotProxy ) {
        try {
            this.doIt( evt, pircBotProxy );
        }
        //catch( IrcPluginException ex ) {
        catch( NullPointerException ex ) {
            log.error( "Plugin misbehaved: " + ex, ex );
        }
        catch ( Throwable ex ) {
            if( System.getProperty("bot.irc.plugins.noStackTraces") == null ) {
                log.error("Plugin misbehaved: " + ex.getMessage(), ex);
            } else {
                log.error("Plugin misbehaved: " + ex);
                if (ex.getCause() != null) {
                    log.error("  Cause: " + ex.getCause());
                }
            }
        }
    }

    public abstract void doIt( T event, IrcBotProxy pircBotProxy ) throws Throwable;    

}// class
