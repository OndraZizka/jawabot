package org.jboss.jawabot;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import cz.dynawest.util.ImmutableProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;




/**
 * Hlavní třída aplikace. Singleton.
 * Taken from my other app, not yet used IIRC.
 * @author Ondřej Žižka
 */
public class JawaBotApp_Poh
{
  private static Logger log = Logger.getLogger( JawaBotApp_Poh.class.getName()  );


  
  /**
   * Spouštěcí metoda -
   * vytvoří objekt aplikace, volá metodu inicializace, a potom run().
   * zpracuje parametry,
   *
   * @param args the command line arguments
   */
  public static void main(String args[]) {

    final JawaBotApp_Poh app = new JawaBotApp_Poh();
    app.init( args );
    //app.run( args );

  }// main()



	


  /**
   * Singleton stuff.
   */
  private static JawaBotApp_Poh instance;
  public static final JawaBotApp_Poh getInstance() {
    if( null == instance ) instance = new JawaBotApp_Poh();
    return instance;
  }


  private Properties applicationParams;
  
  
  // -- Fields --
  
  // Spring Application Context
  private static ApplicationContext ac;
  public static ApplicationContext getAppContext(){ return ac; }
  public static Object getBean(String name) { return ac.getBean(name); }

  
  // Options
  private final Properties options = new Properties();
  public Properties getOptions() { return options; }
  
  public ImmutableProperties getApplicationParams() {
    return new ImmutableProperties(this.applicationParams); 
  } // TODO: Check whether immutable
  

  
  // Constructor
  public JawaBotApp_Poh() {
    JawaBotApp_Poh.instance = this; // TODO: make private, use getInstance only.
    //Thread.dumpStack(); /// Where is this constructor called?
  }
  
  
  
  
  
  
  /**
   * Inicializace objektu aplikace - načte logging, Spring a options
   */
  public void init( String[] args ){
    
    // Parametry aplikace
    this.applicationParams = ZpracujParametry( args );

  
    // nastaveni.properties

    // Který soubor načíst?  Defaultně "nastaveni.properties".
    String sConfigFile = "nastaveni.properties";
    // Zkusíme načíst ze systémových proměnných (nastaveno např. přes -D...=...).
    sConfigFile = System.getProperty( "application.config.file", sConfigFile );
    // Parametry cmdline mají absolutní přednost.
    sConfigFile = this.applicationParams.getProperty( "application.config.file", sConfigFile );
    
    try {
      log.info("Načítám konfiguraci ze souboru "+sConfigFile+" ...");
      this.options.load( new FileInputStream( sConfigFile ) );
    }
    catch( FileNotFoundException ex ) {
      log.log( Level.WARNING,
              "Konfigurační soubor nebyl nalezen: "+sConfigFile, ex );
    }
    catch( IOException ex ) {
      log.log( Level.WARNING,
              "Chyba při čtení konfiguračního souboru souboru: "+sConfigFile, ex );
    }

    // Parametry cmdline mají absolutní přednost.
    this.options.putAll( applicationParams );

    
    // -- Profile --
    String sProfile = System.getenv("profile");
		String sProfileSetBy = "envvar 'profile'";
    if( null == sProfile ){
      sProfile = this.applicationParams.getProperty( "profile" );
			sProfileSetBy = "app param '--profile <profile-name>'";
    }
    if( null == sProfile ){
      sProfile = this.options.getProperty( "profile" );
			sProfileSetBy = "app option 'profile'";
    }
    System.out.println( "Zvolený profil: "+ (sProfile == null ? "(žádný)" : sProfile) );
		System.out.println( "Nastaven z: "+sProfileSetBy );

    
    // Logging 
    String logConfigFile = System.getProperty("java.util.logging.config.file", "conf/"+sProfile+"/logging.properties");
    try {
			log.info("Načítám konfiguraci logování ze souboru: "+logConfigFile);
			log.info("(nastaveno v systémové proměnné java.util.logging.config.file)");
      LogManager.getLogManager().readConfiguration(new FileInputStream(logConfigFile));
    }catch(IOException ex){
      System.out.println("Chyba při načítání nastavení logování ze souboru ["+logConfigFile+"]. Bude použito výchozí.");
    }
    log = Logger.getLogger(JawaBotApp_Poh.class.getName());
    

    // -- Spring Bean Factory --
    
    // Pokud je nastavený parametr profile, pošleme jej do Spring konfigurace.
    if( null != sProfile ){
      System.setProperty( "profile", sProfile );
    }
    ac = new ClassPathXmlApplicationContext("properties/SpringBeans.xml");


    
    // Options
    this.options.putAll( (Properties)ac.getBean( "properties" ) );
    
    
  }// init()
  
  
  


  /**
   * Zpracuje parametry a vrací Properties s názvy parametrů v klíčích.
   * Předpokládaný formát parametrů je tento:
   *    --název.parametru[ hodnotaparametru]
   * Pokud hodnota parametru chybí, výsledné Properties jako jeho hodnotu mají null.   *
   *
   * @returns  Properties s vyskytnuvšími se parametry v klíčích
   *           a případnou hodnotou v hodnotě.
   */
  private static Properties ZpracujParametry( String args[] ){
    
    String sLast = null;
    
    // Zpracování parametrů 
    Properties params = new Properties();
    for( String s : args ){
      if( s.startsWith("--")){
        sLast = s.substring(2);
        params.put( sLast, "" );
      }
      else{
        if( null != sLast )
          params.put( sLast, s );
      }

    }// for( String s : args )
    return params;
  }// 

  




  
  /**
   * Handler to propagate log records to different components. 
   * Not implemented yet.
   */
  static class TestConsoleHandler extends Handler {

    public TestConsoleHandler() {
    }


    @Override
    public void publish( LogRecord rec ) {
      System.out.println( rec.getSourceMethodName()+" - "+rec.getLevel().toString() + ": " + rec.getMessage() );
    }


    @Override
    public void flush() {
    }


    @Override
    public void close() throws SecurityException {
    }
  }
  
  
  

}// class HippurisApplication
