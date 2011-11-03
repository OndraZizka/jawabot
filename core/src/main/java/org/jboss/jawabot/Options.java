
package org.jboss.jawabot;

import org.jboss.jawabot.ex.JawaBotException;


/**
 *  Options - the mapping files, included & excluded paths, and some global options.
 * 
 *  @author Ondrej Zizka
 */
public class Options {


   private String logProfile = null;

   // Options profile - loads configuration from a different file.
   private String profile = null;



   /**
    *  Parse Options from the given app's arguments.
    */
   public static Options createFromParams( String[] args ) throws JawaBotException
   {
      Options options = new Options();


      // For each argument...
      for( int i = 0; i < args.length; i++ ) {
         String arg = args[i];
         if( arg.startsWith("-logProfile=")){  options.logProfile = arg.substring("-logProfile=".length());   continue; }
         if( arg.startsWith("-profile=")){  options.profile = arg.substring("-profile=".length());   continue; }

         throw new JawaBotException("What should I do with this param?  => "+arg);
      }

      return options;
   }




   /**
    *   Checks whether all the paths exist.
    */
   public static void validateOptions( Options options ) throws JawaBotException {

   }// validateOptions()




   public String getLogProfile() {					return this.logProfile;				}
   public String getProfile() {      return this.profile;   }


}// class
