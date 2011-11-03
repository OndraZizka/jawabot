package org.jboss.jawabot;

import org.jboss.jawabot.ex.JawaBotException;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 *  Weld bootstrap wrapper.
 * 
 * @author Ondrej Zizka
 */
public class JawaBotAppWeld {
   
   public static void main(String[] args) throws JawaBotException {

      WeldContainer weld = new Weld().initialize();
      JawaBotApp jawaBotApp = weld.instance().select(JawaBotApp.class).get();
      
      jawaBotApp.main(args);
   }
   
   
}// class

