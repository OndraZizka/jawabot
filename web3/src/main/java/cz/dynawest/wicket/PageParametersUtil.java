package cz.dynawest.wicket;

import org.apache.wicket.PageParameters;

/**
 *  
 *  @author Ondrej Zizka
 */
public class PageParametersUtil
{
   public static org.apache.wicket.PageParameters create( String ... args ){
      
      org.apache.wicket.PageParameters pp = new PageParameters();
      
      if( args.length % 2 != 0 )
         throw new IllegalArgumentException("Arguments must make pairs - i.e. even number.");
      
      for ( int i = 0; i < args.length; i += 2 ) {
         pp.put( args[i], args[i+1] );
      }
      
      return pp;
   }
}


/*/extends org.apache.wicket.PageParameters {

   public PageParameters( String ... args ) {
      
      if( args.length % 2 != 0 )
         throw new IllegalArgumentException("Arguments must make pairs - i.e. even number.");
      
      for ( int i = 0; i < args.length; i += 2 ) {
         this.put( args[i], args[i+1] );
      }
      
   }
   
   
   public PageParameters( String[] keys, Object[] values ) {
      
      if( keys.length != values.length )
         throw new IllegalArgumentException("Number of keys and values must equal.");
      
      for ( int i = 0; i < keys.length; i+ ) {
         this.put( keys[i], values[i] );
      }
      
   }
   
   
   
   
   // Same as in super class
   public PageParameters( String keyValuePairs, String delimiter ) {
      super( keyValuePairs, delimiter );
   }

   public PageParameters( String keyValuePairs ) {
      super( keyValuePairs );
   }

   public PageParameters( Map<String, ?> parameterMap ) {
      super( parameterMap );
   }

   public PageParameters() {
   }

  
   
}// class

/**/