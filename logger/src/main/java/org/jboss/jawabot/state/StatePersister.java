/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jboss.jawabot.state;

import org.jboss.jawabot.ex.JawaBotIOException;
import org.jboss.jawabot.state.beans.StateBean;

/**
 *
 * @author Ondrej Zizka
 */
public interface StatePersister {

   public StateBean load() throws JawaBotIOException;

	public void save( StateBean state ) throws JawaBotIOException;


}// interface




