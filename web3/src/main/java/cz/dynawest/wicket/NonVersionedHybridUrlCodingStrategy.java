
package cz.dynawest.wicket;


import org.apache.wicket.request.target.coding.HybridUrlCodingStrategy;





/** 
 * UrlCodingStrategy that will give the same
 * URL for every version of a page.
 * @author Erik van Oosten
 */
public class NonVersionedHybridUrlCodingStrategy extends HybridUrlCodingStrategy {

   
   public NonVersionedHybridUrlCodingStrategy(String mountPath, Class pageClass) {
      super(mountPath, pageClass, false);
   }

   
   @Override
   protected String addPageInfo(String url, PageInfo pageInfo)
   {
      // Do not add the version number as
      // super.addPageInfo would do.
      return url;
   }
   
}// class NonVersionedHybridUrlCodingStrategy

