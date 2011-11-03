package org.jboss.seam.wicket;

import javax.inject.Inject;
import org.apache.wicket.RequestCycle;
import org.jboss.weld.context.http.HttpRequestContext;

/**
 *  
 *  @author Ondrej Zizka
 */
public class ReqestContext_SeamWebRequestCycleProcessor extends SeamWebRequestCycleProcessor {
   
   //@Inject Conversation conversation;
   @Inject HttpRequestContext requestContext;
   
   /**
    * Prevent conversation usage which throws me "Request context not active".
    */
   @Override
   public void respond(RequestCycle requestCycle)
   {
      this.conversation = null;
      try { 
         super.respond(requestCycle);
      }
      catch (NullPointerException ex ){}
      
   }
   
   
}// class

