
package org.jboss.jawabot;

import org.jboss.jawabot.ex.JawaBotException;


/**
 *  Options - the mapping files, included & excluded paths, and some global options.
 * 
 *  @author Ondrej Zizka
 */
public class Options {


   // Load configuration from file at this path.
   private String configFile = "JawaBotConfig.xml";

   // Options profile - load configuration from JawaBotConfig-<profile>.xml.
   private String profile = null;

   // Logging profile - log4j-<profile>.xml
   private String logProfile = null;

   


   /**
    *  Creates Options, taking system properties into account.
    */
   public Options applySysProps() throws JawaBotException
   {
      String val = System.getProperty("config");
      if( null != val )  this.configFile = val;

      val = System.getProperty("profile");
      if( null != val )  this.profile = val;
      
      val = System.getProperty("logProfile");
      if( null != val )  this.logProfile = val;
      
      return this;
   }
   
   
   /**
    *  Parse Options from the given app's arguments.
    */
   public Options applyAppParams( String[] args ) throws JawaBotException
   {
      // For each argument...
      for( int i = 0; i < args.length; i++ ) {
         String arg = args[i];
         if( arg.startsWith("--config=")){  this.configFile = arg.substring("--config=".length());   continue; }
         if( arg.startsWith("--profile=")){  this.profile = arg.substring("--profile=".length());   continue; }
         if( arg.startsWith("--logProfile=")){  this.logProfile = arg.substring("--logProfile=".length());   continue; }

         throw new JawaBotException("What should I do with this param?  => "+arg);
      }
      return this;
   }




   /**
    *   Checks whether all the paths exist.
    *   @throws  JawaBotException when something's wrong.
    */
   public Options validate() throws JawaBotException {
      return this;
   }// validateOptions()

   
   
   
   
   public String getConfigFile() { return configFile; }
   public String getLogProfile() { return this.logProfile;	}
   public String getProfile() { return this.profile; }


}// class
