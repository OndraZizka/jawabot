
package org.jboss.jawabot.web;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import javax.naming.NamingException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.application.ReloadingClassLoader;
import org.apache.wicket.protocol.http.ReloadingWicketServlet;
import org.apache.wicket.protocol.http.WicketServlet;
import org.jboss.weld.wicket.BeanManagerLookup;
import org.mortbay.component.LifeCycle;
import org.mortbay.component.LifeCycle.Listener;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.plus.naming.Resource;
import org.mortbay.jetty.servlet.*;
import org.mortbay.jetty.webapp.Configuration;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.util.StringUtil;
import org.mortbay.xml.XmlConfiguration;



/**
 * 
		<init-param>
			<param-name>applicationClassName</param-name>
			<param-value>cz.dw.test.WicketApplication</param-value>
		</init-param>
 * 
 * @author Ondrej Zizka
 */
public class RunInJetty
{
   private static final Logger log = Logger.getLogger(RunInJetty.class);

   
   public static void main( String[] args ){
      run();
   }

   public static void run()
   {

      Server server = new Server( 8080 );
      //Context ctx = new Context( server, "/", Context.NO_SECURITY | Context.SESSIONS );
      WebAppContext ctx = new WebAppContext( server, null, "/"); /// new approach
      


      
      // Static content.
      
      /* I think the piece of the puzzle you are missing is that your
      static content is not being served. You need to add a servlet
      that will serve the html, images etc for you: */
      ServletHolder defaultSH = new ServletHolder( new org.mortbay.jetty.servlet.DefaultServlet() );
      
      
      String staticDirPath = System.getProperty( "jawabot.web.staticFiles");
      File staticDir;
      if( null != staticDirPath ){
         log.debug("  Using env var jawabot.web.staticFiles .");
         staticDir = new File(staticDirPath);
      }
      else {
         // Use classes path as default
         URL staticDirURL = RunInJetty.class.getResource("/org/jboss/jawabot/web/RunInJetty.class");
         try {
            staticDir = new File( staticDirURL.toURI() );
         } catch( URISyntaxException ex ) {
            log.error("  Can't read a file from URI: " + staticDirURL );
            staticDir = new File("");
         }
      }
      
      String staticDirCanonPath = "";
      try {
         staticDirCanonPath = staticDir.getCanonicalPath();
      } catch (IOException ex) {
         log.error("  Non-existent path: " + staticDir.getPath());
      }
      
      log.info("  Static files are in: "
              + "\n        " + staticDir.getAbsolutePath()
              + "\n   ==   " + StringUtils.defaultIfEmpty(staticDirCanonPath, "non-existent path") );

      defaultSH.setInitParameter( "resourceBase", staticDirCanonPath );
      ctx.addServlet( defaultSH, "/css/*" );
      ctx.addServlet( defaultSH, "/img/*" );
      ctx.addServlet( defaultSH, "/js/*" );
      ctx.addServlet( defaultSH, "/favicon.ico" );

      
      
      // Another approach for static files.
      /* Does not work, because ResourceHandler can't chain the previous handlers - not a HandlerContainer.
      ResourceHandler resourceHandler = new ResourceHandler();
      resourceHandler.setResourceBase("./src/main/java/org/jboss/jawabot/web/files");
      //HandlerWrapper handlerWrapper = new HandlerWrapper();
      //handlerWrapper.setHandler( resourceHandler );
      //ctx.addHandler( handlerWrapper );
      ctx.addHandler( resourceHandler );
      ctx.addHandler(new ServletHandler(){{ addServlet(wicketSH); }});
      /**/

      
      
      
      
      // Wicket.
      final ServletHolder wicketSH = new ServletHolder( new MyReloadingWicketServlet() );
      wicketSH.setInitParameter( "applicationClassName", WicketApplication.class.getName() );
      ctx.addServlet( wicketSH, "/*" );
      //ctx.setAttribute( JawaBotApp.ID.JAWABOT, JawaBotApp.getJawaBot() );


      /*/
      FilterHolder filterHolder = new FilterHolder( new WicketFilter() );
      filterHolder.setInitParameter("applicationClassName", cz.dw.test.WicketApplication.class.getName() );
      root.addFilter( filterHolder, "/*" , Handler.ALL );
      /**/


      
      
      
      
      // Trying to integrate Weld. But found better - weld-wicket.
      // http://docs.jboss.org/weld/reference/1.0.0/en-US/html/viewlayers.html#d0e5200
      // org.jboss.weld.wicket.WeldApplication

      
      //new Resource("blah", new Object);
      //server.addHandler( new org.mortbay.jetty.webapp.WebAppContext("src/main/webapp", "/my-context") );
      //ctx.addHandler( new );


      /**/
      try {
         
         // See Jetty examples - FromXmlConfiguration.java
         String confXml = IOUtils.toString( RunInJetty.class.getResourceAsStream( "/WEB-INF/jetty-env.xml" ) );
         XmlConfiguration configuration = new XmlConfiguration(confXml); 
         configuration.configure(server);
         
         //BeanManager
         new org.mortbay.jetty.plus.naming.Resource( ctx, /*"BeanManager"*/ BeanManagerLookup.getBeanManagerJndiName(), 
            new javax.naming.Reference(
               "javax.enterprise.inject.spi.BeanManager",
               "org.jboss.weld.resources.ManagerObjectFactory", null )
         );
      } catch ( Exception ex ) {
         log.error( ex );
      }
      /**/



      try {
         server.start();
      } catch ( Exception ex ) {
         ex.printStackTrace();
      }

   }

}// class



class MyReloadingWicketServlet extends ReloadingWicketServlet 
{
    static {
        ReloadingClassLoader.excludePattern( "org.apache.wicket.*" );
        ReloadingClassLoader.includePattern( "org.jboss.jawabot.*" );
    }
}