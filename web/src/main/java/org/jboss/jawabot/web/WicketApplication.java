package org.jboss.jawabot.web;

import java.util.Iterator;
import org.jboss.jawabot.web._pg.LoginPage;
import org.jboss.jawabot.web._pg.PasteBinShowPage;
import org.jboss.jawabot.web._pg.LeavePage;
import org.jboss.jawabot.web._pg.ResourcePage;
import org.jboss.jawabot.web._pg.HomePage;
import org.jboss.jawabot.web._pg.PasteBinPage;
import org.jboss.jawabot.web._pg.TakePage;
import cz.dynawest.wicket.NonVersionedHybridUrlCodingStrategy;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.devutils.inspector.InspectorPage;
import org.apache.wicket.request.target.coding.MixedParamHybridUrlCodingStrategy;
import org.apache.wicket.request.target.coding.QueryStringUrlCodingStrategy;
import org.jboss.jawabot.mod.web.IPageMount;
import org.jboss.jawabot.mod.web.MountProxy;
import org.jboss.jawabot.web._base.BaseLayoutPage;
import org.jboss.jawabot.web._base.BaseLayoutPage_Vut;
import org.jboss.jawabot.web._pg.test.CheckBoxMultipleChoiceTestPage;
import org.jboss.jawabot.web._pg.GroupPage;
import org.jboss.jawabot.web._pg.test.ChessGridTestPage;
import org.jboss.seam.wicket.InjectingSeamApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * Extends SeamApplication instead of WebApplication - I am about to try CDI with Seam-Wicket.
 */
public class WicketApplication extends InjectingSeamApplication
{
   private static final Logger log = LoggerFactory.getLogger( WicketApplication.class );

   
   @Inject private Instance<IPageMount> pageMounts;



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
      
      
      mountBookmarkablePage("cbmc", CheckBoxMultipleChoiceTestPage.class);
      mountBookmarkablePage("chess", ChessGridTestPage.class);
      mountBookmarkablePage("debug", InspectorPage.class);
      mountBookmarkablePage("cssTest",  BaseLayoutPage.class);
      mountBookmarkablePage("vutTest",  BaseLayoutPage_Vut.class);
      
      
      // Mount plugin pages.
      for( IPageMount pm : this.pageMounts ){
         log.info("  Mounting pages for " + pm.getClass().getName() );
         try{
            pm.mount( new MountProxy(this) );
         } catch ( Throwable ex ){
            log.error("Mounter misbehaved: " + ex.getMessage(), ex);
         }
      }
      
      
      getMarkupSettings().setStripWicketTags(true);
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
