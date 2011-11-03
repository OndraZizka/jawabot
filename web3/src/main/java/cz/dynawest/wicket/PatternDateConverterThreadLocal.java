
package cz.dynawest.wicket;

import org.apache.wicket.datetime.PatternDateConverter;

/** 
 *  
 *  @author Ondrej Zizka
 */
public class PatternDateConverterThreadLocal extends ThreadLocal<PatternDateConverter> {
   
   private final String datePattern;
   private final boolean applyTimeZoneDifference;

   public PatternDateConverterThreadLocal( String datePattern, boolean applyTimeZoneDifference ) {
      this.datePattern = datePattern;
      this.applyTimeZoneDifference = applyTimeZoneDifference;
   }


   @Override
   protected PatternDateConverter initialValue() {
      return new PatternDateConverter( datePattern, applyTimeZoneDifference );
   }
   

}// class
