/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jboss.jawabot.config;

import org.jboss.jawabot.config.beans.ConfigBean;
import org.jboss.jawabot.ex.JawaBotIOException;

/**
 *
 * @author Ondrej Zizka
 */
public interface ConfigPersister {

   public ConfigBean load() throws JawaBotIOException;

	public void save( ConfigBean bot ) throws JawaBotIOException;


}// interface




