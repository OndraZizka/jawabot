
package org.jboss.jawabot.web;


import java.util.logging.*;
import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.WebSession;


/**
 *
 * @author Ondrej Zizka
 */
public class JawaBotSession extends WebSession
{
   private static final Logger log = Logger.getLogger( JawaBotSession.class.getName() );


   private String loggedUser;
   public String getLoggedUser() {      return loggedUser;   }
   public void setLoggedUser(String loggedUser) {      this.loggedUser = loggedUser;   }

   
   public JawaBotSession(Request request) {
      super(request);
      log.info("JawaBotSession being instantiated.");
   }

}// class JawaBotSession
