package org.jboss.jawabot.plugin.jira.core;

import org.jboss.jawabot.ex.JawaBotPluginEx;


/**
 *
 * @author Ondrej Zizka
 */
public class JiraBotException extends JawaBotPluginEx {

    public JiraBotException( Throwable cause ) {
        super( cause );
    }


    public JiraBotException( String message, Throwable cause ) {
        super( message, cause );
    }


    public JiraBotException( String message ) {
        super( message );
    }


}// class
