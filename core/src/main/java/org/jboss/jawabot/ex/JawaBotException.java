/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jboss.jawabot.ex;

/**
 *
 * @author Ondrej Zizka
 */
public class JawaBotException extends Exception {

	public JawaBotException(Throwable cause) {
		super(cause);
	}

	public JawaBotException(String message, Throwable cause) {
		super(message, cause);
	}

	public JawaBotException(String message) {
		super(message);
	}

	public JawaBotException() {
	}

}
