
package cz.dynawest.wicket;


import org.apache.wicket.request.target.coding.MixedParamHybridUrlCodingStrategy;





/** 
 * UrlCodingStrategy that will give the same
 * URL for every version of a page.
 * @author Erik van Oosten
 */
public class NonVersionedMixedParamHybridUrlCodingStrategy extends MixedParamHybridUrlCodingStrategy {

   
   public NonVersionedMixedParamHybridUrlCodingStrategy(String mountPath, Class pageClass, String[] parameterNames ) {
      super( mountPath, pageClass, parameterNames );
   }

   
   @Override
   protected String addPageInfo(String url, PageInfo pageInfo)
   {
      // Do not add the version number as
      // super.addPageInfo would do.
      return url;
   }
   
}// class NonVersionedHybridUrlCodingStrategy

