
package org.jboss.jawabot.plugin.jira.config.core;

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
   public static Options createFromParams( String[] args ) throws JiraBotException
   {
      Options options = new Options();


      // For each argument...
      for( int i = 0; i < args.length; i++ ) {
         String arg = args[i];
         if( arg.startsWith("-logProfile=")){  options.logProfile = arg.substring("-logProfile=".length());   continue; }
         if( arg.startsWith("-profile=")){  options.profile = arg.substring("-profile=".length());   continue; }

         throw new JiraBotException("What should I do with this param?  => "+arg);
      }

      return options;
   }




   /**
    *   Checks whether all the paths exist.
    */
   public static void validateOptions( Options options ) throws JiraBotException {

   }// validateOptions()




   public String getLogProfile() {					return this.logProfile;				}
   public String getProfile() {      return this.profile;   }


}// class
