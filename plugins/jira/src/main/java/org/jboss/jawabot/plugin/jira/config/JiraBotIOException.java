
package org.jboss.jawabot.plugin.jira.config;

import org.jboss.jawabot.plugin.jira.core.JiraBotException;

/**
 *
 * @author Ondrej Zizka
 */
public class JiraBotIOException extends JiraBotException {

    public JiraBotIOException( String message ) {
        super( message );
    }


    public JiraBotIOException( String message, Throwable cause ) {
        super( message, cause );
    }


    public JiraBotIOException( Throwable cause ) {
        super( cause );
    }

}// class
