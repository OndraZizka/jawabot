package org.jboss.jawabot.mod.web;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;
import org.apache.wicket.util.lang.PackageName;

/**
 *  Restricts access to the app object in IPageMount implementations
 *  - they can only call mount-related methods.
 * 
 *  @author Ondrej Zizka
 */
public class MountProxy {
   
   private WebApplication wicketApp;

   public MountProxy( WebApplication wicketApp ) {
      this.wicketApp = wicketApp;
   }

   
   
   
   public final void mountSharedResource( String path, String resourceKey ) {
      wicketApp.mountSharedResource( path, resourceKey );
   }

   public final <T extends Page> void mountBookmarkablePage( String path, String pageMapName, Class<T> bookmarkablePageClass ) {
      wicketApp.mountBookmarkablePage( path, pageMapName, bookmarkablePageClass );
   }

   public final <T extends Page> void mountBookmarkablePage( String path, Class<T> bookmarkablePageClass ) {
      wicketApp.mountBookmarkablePage( path, bookmarkablePageClass );
   }

   public final void mount( String path, PackageName packageName ) {
      wicketApp.mount( path, packageName );
   }

   public final void mount( IRequestTargetUrlCodingStrategy encoder ) {
      wicketApp.mount( encoder );
   }

   public final String getInitParameter( String key ) {
      return wicketApp.getInitParameter( key );
   }

   public String getConfigurationType() {
      return wicketApp.getConfigurationType();
   }

   public final void addIgnoreMountPath( String path ) {
      wicketApp.addIgnoreMountPath( path );
   }
   
   
}// class

