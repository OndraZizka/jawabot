package org.jboss.jawabot.web;

import cz.dynawest.wicket.LoggingUtils;
import org.jboss.jawabot.web._pg.LoginPage;
import org.jboss.jawabot.web._pg.PasteBinShowPage;
import org.jboss.jawabot.web._pg.LeavePage;
import org.jboss.jawabot.web._pg.ResourcePage;
import org.jboss.jawabot.web._pg.HomePage;
import org.jboss.jawabot.web._pg.PasteBinPage;
import org.jboss.jawabot.web._pg.TakePage;
import cz.dynawest.wicket.NonVersionedHybridUrlCodingStrategy;
import cz.dynawest.wicket.PatternDateConverterThreadLocal;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.devutils.inspector.InspectorPage;
import org.apache.wicket.request.target.coding.MixedParamHybridUrlCodingStrategy;
import org.apache.wicket.request.target.coding.QueryStringUrlCodingStrategy;
import org.jboss.jawabot.web._base.BaseLayoutPage;
import org.jboss.jawabot.web._base.BaseLayoutPage_Vut;
import org.jboss.jawabot.web._pg.GroupPage;
import org.jboss.seam.wicket.SeamApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.jboss.weld.wicket.WeldApplication;


/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * Extends SeamApplication instead of WebApplication - I am about to try CDI with Seam-Wicket.
 * 
 * @see cz.dw.test.Start#main(String[])
 */
public class WicketApplication extends SeamApplication
{
   static { LoggingUtils.setFormatOfAllAppenders(); }
   //private static final Logger log = LoggerFactory.getLogger( WicketApplication.class );
   private static final Logger log = LoggerFactory.getLogger( WicketApplication.class );
   static { LoggingUtils.setFormatOfAllAppenders(); }

   private static PatternDateConverterThreadLocal patternDateConverterTL = new PatternDateConverterThreadLocal("yyyy-MM-dd", true);
   public static PatternDateConverterThreadLocal getPatternDateConverterTL() { return patternDateConverterTL; }



   /**
   * Constructor
   */
   public WicketApplication() {
      super();
   }


   /**
   * @see org.apache.wicket.Application#getHomePage()
   */
   @Override  public Class getHomePage() {
      return HomePage.class;
   }


   // Init
   @Override
   protected void init() {
      System.out.println("----  Wicket init() ----");
      //mountBookmarkablePage("res",  ResourcesPage.class);
      //mountBookmarkablePage("res/",  ResourcePage.class);
      mount(new MixedParamHybridUrlCodingStrategy("res", ResourcePage.class, new String[]{"name"}));
      mountBookmarkablePage("group", GroupPage.class);
      mountBookmarkablePage("take",  TakePage.class);
      mountBookmarkablePage("leave", LeavePage.class);
      mountBookmarkablePage("pastebin/new", PasteBinPage.class);
      mount(new NonVersionedHybridUrlCodingStrategy("pastebin", PasteBinShowPage.class));
      
      //mountBookmarkablePage("login", LoginPage.class);
      //mount(new NonVersionedHybridUrlCodingStrategy("login", LoginPage.class));
      //mount(new MixedParamHybridUrlCodingStrategy("login", LoginPage.class, new String[]{"logout"}));
      mount(new QueryStringUrlCodingStrategy("login", LoginPage.class));
      
      
      mountBookmarkablePage("debug", InspectorPage.class);
      mountBookmarkablePage("cssTest",  BaseLayoutPage.class);
      mountBookmarkablePage("vutTest",  BaseLayoutPage_Vut.class);
   }


  // Shutdown
  @Override protected void onDestroy() {
    System.out.println( "---- onDestroy() ----" );
  }


   @Override
   public Session newSession(Request request, Response response) {
      return new JawaBotSession(request);
   }

}
