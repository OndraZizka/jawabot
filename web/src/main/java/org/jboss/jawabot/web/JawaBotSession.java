
package org.jboss.jawabot.web;


import org.slf4j.Logger; import org.slf4j.LoggerFactory; 
import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.WebSession;


/**
 *
 * @author Ondrej Zizka
 */
public class JawaBotSession extends WebSession
{
   private static final Logger log = LoggerFactory.getLogger( JawaBotSession.class );


   private String loggedUser;
   public String getLoggedUser() {      return loggedUser;   }
   public void setLoggedUser(String loggedUser) {      this.loggedUser = loggedUser;   }
   
   public void logout(){
      this.setLoggedUser(null);
   }
   
   public boolean isUserLogged(){
      return this.loggedUser != null;
   }

   
   public JawaBotSession(Request request) {
      super(request);
      log.info("JawaBotSession being instantiated.");
   }

}// class JawaBotSession
