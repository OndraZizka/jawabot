package cz.dynawest.wicket;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import org.apache.log4j.Appender;
import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.LogManager;
import org.slf4j.Logger; import org.slf4j.LoggerFactory; 
import org.apache.wicket.protocol.http.WebApplication;
import org.jboss.weld.util.collections.EnumerationList;

/**
 *  Logging hack - to force layout to be single line.
 * 
 *  @author Ondrej Zizka
 */
public class LoggingUtils {

   public static List<Appender> getAppenders() throws Exception{
              
      try {
         //Enumeration appersE = LogManager.getRootLogger().getAllAppenders();
         //List<Appender> appers = new EnumerationList<Appender>( appersE );
         
         Class<? extends LogManager> lmc = WebApplication.class.getClassLoader().loadClass("org.apache.log4j.LogManager").asSubclass(LogManager.class);
         Enumeration<org.apache.log4j.Logger> loggersE = ( Enumeration<org.apache.log4j.Logger> ) lmc.getDeclaredMethod("getCurrentLoggers").invoke(null);
         List<Logger> loggers = new EnumerationList<Logger>( loggersE );

         Set<Appender> appers = new TreeSet<Appender>();
         for ( Logger logger : loggers ) {
            //System.out.println( " **** Logger: " + logger.getName() );///
            for( Appender a : new EnumerationList<Appender>( logger.getAllAppenders() ) ){
               //System.out.println( "   *** Appender: " + a.getName() );///
               appers.add( a );
            }
         }
         
         return new ArrayList<Appender>( appers );
      }         
      catch ( Exception ex ) {
         throw ex;
      }
   }
   
   public static void setFormatOfAllAppenders() {
      //System.out.println( "***************** Jdem na to! *****************" );///
      setFormatOfAllAppenders("%d{HH:mm:ss.SSS} %-5p [%t] %c  %m%n");
   }
   
   public static void setFormatOfAllAppenders( final String format ) {
      try {
         for( Appender a : getAppenders() ) {
            System.out.println( "  Original appender / layout: " +  a.getName() +  " / " + a.getLayout() );
            a.setLayout( new EnhancedPatternLayout( format ) );
         }
      } catch ( Exception ex ) {
         Logger.getLogger( LoggingUtils.class ).warn("Error when setting single line log format: " + ex);
      }
   }
   
}// class

