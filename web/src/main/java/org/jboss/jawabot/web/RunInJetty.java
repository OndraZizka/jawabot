
package org.jboss.jawabot.web;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Hashtable;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.application.ReloadingClassLoader;
import org.apache.wicket.protocol.http.ReloadingWicketServlet;
//import org.jboss.weld.wicket.BeanManagerLookup;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.plus.naming.EnvEntry;
import org.mortbay.jetty.plus.naming.NamingEntryUtil;
import org.mortbay.jetty.plus.naming.Resource;
import org.mortbay.jetty.servlet.*;
import org.mortbay.naming.NamingUtil;
import org.mortbay.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



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
   private static final Logger log = LoggerFactory.getLogger(RunInJetty.class);

   
   public static void main( String[] args ){
      run();
   }

   public static void run()
   {

      Server server = new Server( 8080 );
      Context ctx = new Context( server, "/", Context.NO_SECURITY | Context.SESSIONS );


      // Static content.
      
      /* I think the piece of the puzzle you are missing is that your
      static content is not being served. You need to add a servlet
      that will serve the html, images etc for you: */
      ServletHolder defaultSH = new ServletHolder( new org.mortbay.jetty.servlet.DefaultServlet() );
      
      
      String staticDirPath = System.getProperty("jawabot.web.staticFiles");
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

      
      
      // Wicket.
      final ServletHolder wicketSH = new ServletHolder( new MyReloadingWicketServlet() );
      wicketSH.setInitParameter( "applicationClassName", WicketApplication.class.getName() );
      ctx.addServlet( wicketSH, "/*" );
      //ctx.setAttribute( JawaBotApp.ID.JAWABOT, JawaBotApp.getJawaBot() );


      try {
         
         //BeanManager

         // See Jetty examples - FromXmlConfiguration.java
         //String confXml = IOUtils.toString( RunInJetty.class.getResourceAsStream( "/WEB-INF/jetty-env.xml" ) );
         XmlConfiguration confJettyEnv = new XmlConfiguration( RunInJetty.class.getResourceAsStream("/WEB-INF/jetty-env.xml") ); 
         confJettyEnv.configure(ctx);
         
         
         javax.naming.Reference ref = 
               new javax.naming.Reference(
                  javax.enterprise.inject.spi.BeanManager.class.getName(),
                  //"org.jboss.weld.resources.ManagerObjectFactory",
                  org.jboss.weld.resources.ManagerObjectFactory.class.getName(),
                  null 
               );

         //BeanManagerLookup.getBeanManagerJndiName();
         Resource resource = new org.mortbay.jetty.plus.naming.Resource( ctx, "BeanManager", ref );
         EnvEntry envEntry = new org.mortbay.jetty.plus.naming.EnvEntry( ctx, "BeanManager", ref, true );
         

         Hashtable<String, String> hashtable = new Hashtable();
         hashtable.put( "java.naming.factory.initial",  "org.mortbay.naming.InitialContextFactory" );
         hashtable.put( "java.naming.factory.url.pkgs", "org.mortbay.naming" );
         javax.naming.Context ic = new org.mortbay.naming.InitialContextFactory().getInitialContext(hashtable);
         
         
         
         //InitialContext ic = new InitialContext();
         NameParser parser = ic.getNameParser("");
         //Name prefix = NamingEntryUtil.getNameForScope(scope);
         Name prefix = parser.parse("").add("_");

         //bind the NamingEntry into the context
         Name namingEntryName = NamingEntryUtil.makeNamingEntryName(parser, "BeanManager");
         namingEntryName.addAll(0, prefix);
         String namingEntryNameString = namingEntryName.toString();
         NamingUtil.bind( ic, namingEntryNameString, envEntry );

         //bind the object as well
         Name objectName = parser.parse("BeanManager");
         objectName.addAll(0, prefix);
         String objectNameString = objectName.toString();
         NamingUtil.bind(ic, objectNameString, ref);
         NamingUtil.bind(ic, "BeanManager", ref);
         NamingUtil.bind(ic, "/BeanManager", ref);
         NamingUtil.bind(ic, "env/BeanManager", ref);
         NamingUtil.bind(ic, "/env/BeanManager", ref);
         NamingUtil.bind(ic, "_/BeanManager", ref);
         NamingUtil.bind(ic, "_/env/BeanManager", ref);
         
         NamingEnumeration<NameClassPair> list = ic.list("/");
         while( list.hasMore() ){
            System.out.println("  @@@@@@@  " + list.next().toString() );
         }

         
         // Add WELD listener by hand.
         ctx.addEventListener( new org.jboss.weld.environment.servlet.Listener() );
         
         
         // DefaultJndiBeanManagerProvider looks for "java:comp/BeanManager";
         // ServletContainerJndiBeanManagerProvider looks for "java:comp/env/BeanManager";
         
      }
      catch ( Exception ex ) {
         log.error( "  Error putting BeanManager to JNDI: " + ex, ex );
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