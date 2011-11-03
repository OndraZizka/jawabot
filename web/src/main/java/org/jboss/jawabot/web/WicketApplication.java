package org.jboss.jawabot.web;

import org.jboss.jawabot.web._pg.LoginPage;
import org.jboss.jawabot.web._pg.PasteBinShowPage;
import org.jboss.jawabot.web._pg.LeavePage;
import org.jboss.jawabot.web._pg.ResourcePage;
import org.jboss.jawabot.web._pg.HomePage;
import org.jboss.jawabot.web._pg.PasteBinPage;
import org.jboss.jawabot.web._pg.TakePage;
import cz.dynawest.wicket.NonVersionedHybridUrlCodingStrategy;
import cz.dynawest.wicket.PatternDateConverterThreadLocal;
import java.util.logging.Logger;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.devutils.inspector.InspectorPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.target.coding.MixedParamHybridUrlCodingStrategy;
import org.jboss.jawabot.web._base.BaseLayoutPage_1;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see cz.dw.test.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{
   private static final Logger log = Logger.getLogger( WicketApplication.class.getName() );

   private static PatternDateConverterThreadLocal patternDateConverterTL = new PatternDateConverterThreadLocal("yyyy-MM-dd", true);
   public static PatternDateConverterThreadLocal getPatternDateConverterTL() { return patternDateConverterTL; }



   /**
   * Constructor
   */
   public WicketApplication() {
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
      mountBookmarkablePage("res",   ResourcePage.class);
      mountBookmarkablePage("take",  TakePage.class);
      mountBookmarkablePage("leave", LeavePage.class);
      mountBookmarkablePage("debug", InspectorPage.class);
      mountBookmarkablePage("pastebin/new", PasteBinPage.class);
      mountBookmarkablePage("cssTest", BaseLayoutPage_1.class);
      mount(new NonVersionedHybridUrlCodingStrategy("pastebin", PasteBinShowPage.class));
      //mountBookmarkablePage("group", GroupPage.class);
      //mountBookmarkablePage("login", LoginPage.class);
      //mount(new NonVersionedHybridUrlCodingStrategy("login", LoginPage.class));
      mount(new MixedParamHybridUrlCodingStrategy("login", LoginPage.class, new String[]{"logout"}));
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
