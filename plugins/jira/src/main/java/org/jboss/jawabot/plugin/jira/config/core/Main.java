package org.jboss.jawabot.plugin.jira.config.core;

import java.io.IOException;
import java.net.URL;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jibble.pircbot.IrcException;

public class Main 
{
   private static final Logger log = Logger.getLogger( Main.class );


   public static void main( String[] args ) {

      System.out.println( "JIRA IRC Bot" );
      log.info("JIRA IRC Bot");


      // Options.
      Options options = null;
      try {
         options = Options.createFromParams(args);
      } catch( JiraBotException ex ) {
         log.error("Error parsing arguments: " + ex.getMessage());
         System.exit(1);
      }


      // Different logging profile?
      String logProfile = options.getLogProfile();
      if( logProfile != null )
      {
         String logPath = "log4j-" + logProfile + ".properties";
         log.debug("  Loading log config from: "+logPath );
         URL conf = Main.class.getClassLoader().getResource( logPath );
         if( null == conf ){
            log.warn("  Can't find logging profile: "+logPath);
         }else{
            LogManager.resetConfiguration();
            org.apache.log4j.PropertyConfigurator.configure( conf );
         }
      }


      // Execution...
      try {
         doSomeIdling( options );
      } catch( IOException ex ) {
         ex.printStackTrace();
      } catch( IrcException ex ) {
         ex.printStackTrace();
      }
   }



   private static void doSomeIdling( Options options ) throws IOException, IrcException {
			
      JiraBot bot = new JiraBot();
      bot.setVerbose(true); // Enable debugging output.

      // Connect
      try {
         bot.init( options );
         bot.start();
      } catch( JiraBotException ex ){
         //java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
         log.error(ex);
      }
   }

}// class
