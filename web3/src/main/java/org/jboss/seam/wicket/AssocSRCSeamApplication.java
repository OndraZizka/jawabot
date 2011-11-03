package org.jboss.seam.wicket;

import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycleProcessor;
import org.apache.wicket.protocol.http.WebResponse;

/**
 *  
 *  @author Ondrej Zizka
 */
public abstract class AssocSRCSeamApplication extends SeamApplication {

   /**
    * Override to return our Seam-specific request cycle
    * 
    * @see SeamRequestCycle
    */
   @Override
   public RequestCycle newRequestCycle(final Request request, final Response response)
   {
      // Trying to prevent "no context for RequestScoped"...
      //new org.jboss.weld.context.http.HttpRequestContextImpl().associate( ((WebRequest) request).getHttpServletRequest() );
      
      return new AssociatingSeamRequestCycle(this, (WebRequest) request, (WebResponse) response);
   }

   @Override
   protected Class<? extends SeamWebRequestCycleProcessor> 
   getWebRequestCycleProcessorClass() 
   { 
      return ReqestContext_SeamWebRequestCycleProcessor.class;
   }
   
   
}// class

