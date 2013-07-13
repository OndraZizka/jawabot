package org.jboss.jawabot;

import java.io.File;
import java.io.IOException;
import org.jboss.jawabot.ex.UnknownResourceException;
import org.jboss.jawabot.ex.JawaBotIOException;
import org.jboss.jawabot.ex.JawaBotException;
import java.util.*;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.jawabot.config.JaxbGenericPersister;
import org.jboss.jawabot.config.beans.ConfigBean;
import org.jboss.jawabot.config.beans.PluginBean;
import org.slf4j.Logger; import org.slf4j.LoggerFactory; 
import org.jboss.jawabot.groupmgr.GroupManager;
import org.jboss.weld.environment.se.jpa.EmfProperties;
import org.jboss.weld.environment.se.jpa.QEmfProperties;



/**
 * JawaBot core.
 *
 * @author Ondrej Zizka
 */
@ApplicationScoped
public class JawaBot
{
    private static final Logger log = LoggerFactory.getLogger( JawaBot.class );



    // Config
    @Produces @FromJawaBot
    public ConfigBean getConfig() {
        return config;
    }
    private ConfigBean config;
    
    
    // MailUtils
    private final MailUtils mailUtils = new MailUtils( this.config );
    @Produces @FromJawaBot public MailUtils getMailUtils() { return this.mailUtils; }

    
    // EMF properties. CDI doesn't work in EntityManagerStoreImpl.
    @Produces @QEmfProperties @ApplicationScoped public EmfProperties getEntityManagerFactoryProperties(){
        final EmfProperties props = new EmfProperties();
        if( config.persistence == null || config.persistence.jdbc == null ){
            log.warn("No persistence JDBC config found, using defaults - MySQL, localhost:3306, jawabot/jawabot");
        }
        else {
            props.setProperty("hibernate.connection.url", config.persistence.jdbc.url );
            props.setProperty("hibernate.connection.username", config.persistence.jdbc.user );
            props.setProperty("hibernate.connection.password", config.persistence.jdbc.pass );
            props.setProperty("hibernate.connection.driver_class", config.persistence.jdbc.driver );
        }
        return props;
    }
    
    
    private boolean initialized = false;
    public boolean isInitialized() {      return initialized;   }

    private int quitPassword = new Random().nextInt(1000);
    public int getQuitPassword() { return quitPassword; }




    // GroupManager
    private GroupManager groupManager = new GroupManager();
    public GroupManager getGroupManager() { return groupManager; }
    public void setGroupManager(GroupManager groupManager) { this.groupManager = groupManager; }






    /**
     * Creates the bot, loads the configuration, initializes and returns the bot.
     */
    public static JawaBot create( ConfigBean cb ) throws JawaBotException {
       log.debug("Creating JawaBot.");
       JawaBot bot = new JawaBot(0);
       bot.applyConfig(cb);
       bot.init();
       return bot;
    }


     /** Const - to prevent instantiation from CDI. */
     private JawaBot( int a ) {
         log.debug("Constructing JawaBot.");
     }



    /**
     * Init - loads the state (resources reservations) and stores the quit password.
     */
    public synchronized void init() throws JawaBotIOException, UnknownResourceException {
       log.info("Initializing...");
       if( this.initialized )
          log.warn("Already initialized.");

       // Log the quit password.
       log.info("");
       log.info("                        ***  QUIT PASSWORD: "+this.quitPassword+"  ***");
       log.info("");

       // Store the quit password.
       try {
          File passFile = new File("quit" + this.quitPassword + ".txt");
          FileUtils.touch( passFile );
          FileUtils.forceDeleteOnExit( passFile );
       } catch (IOException ex) {
          log.error("Can't write password file: "+ex, ex);
       }

       this.initialized = true;
    }



    /**
     * 
     * @param pluginID        Plugin ID, like "jira" or "social"
     * @param jaxbBeanClass   JAXB class to read options to.
     */
    public <TConfigBean> TConfigBean readCustomConfig( String pluginID, Class<TConfigBean> jaxbBeanClass ) throws JawaBotException  {
         String configPathStr = this.getConfig().getPluginsMap().get( pluginID ).getConfigPath();
         if( configPathStr == null ){
             //throw new JawaBotException("No config file defined for plugin ID: " + pluginID);
             log.info("No config file defined for plugin ID: " + pluginID);
             return null;
         }


         String configDirStr = StringUtils.defaultIfEmpty( this.config.getReadFrom().getPath(), "." );
         File configDir = new File( configDirStr ).getParentFile();
         File configPath = new File( configDir, configPathStr );
         log.info("Looking for Jira plugin config at: " + configPath.getAbsolutePath() );

         JaxbGenericPersister<TConfigBean> persister = new JaxbGenericPersister( configPath.getPath(), jaxbBeanClass );
         try {
             TConfigBean pluginConfigBean = persister.load();
             return pluginConfigBean;
         } catch( JawaBotIOException ex ) {
             throw new JawaBotException("Error loading config file " + configPathStr + " defined for plugin ID '" + pluginID + "':\n\t" + ex.getMessage(), ex );
         }
    }




    /**
     * Configures this bot according to the given config bean (possibly read from XML).
     * @param config
     */
    public void applyConfig( ConfigBean config )
    {
       this.config = config;
       if( config.getReadFrom() != null )
           config.setReadFrom( config.getReadFrom() );
    }






    /**
     *  Shutdown hook.
     */
    void waitForShutdown() {
       synchronized( this.shutdown ){
          try {
             this.shutdown.wait();
          } catch (InterruptedException ex) {
             log.warn("Interrupted.");
          }
       }
    }

    /**
     * This should let waiting threads (main?) continue and finish.
     */
    void notifyShutdown() {
       synchronized( this.shutdown ){
          log.warn("Shutting down.");
          this.shutdown.notifyAll();
       }
    }

    Object shutdown = new Object();


    
    /**
     *  Looks up the config file for given plugin.
     *           
     *  @throws JawaBotException If no config is specified for it and the default file doesn't exist either, 
     *                           or if it is specified but doesn't exist.
     */
    public File getConfigFileForPlugin(String pluginID ) throws JawaBotException {
        
        // Root config file directory - plugin configs are relative to it.
        File rootConfigDir = this.config.getReadFrom().getParentFile();
        
        final PluginBean pluginConfig = this.config.getPluginsMap().get( pluginID );

        File configFile;
        if( null != pluginConfig &&  pluginConfig.getConfigPath() != null ){
            configFile = new File( rootConfigDir, pluginConfig.getConfigPath() );
            if( ! configFile.exists() )
                throw new JawaBotIOException("Plugin config file referenced in '"+this.config.getReadFrom()+"' not found: " + configFile.getAbsolutePath());
        }
        else{
            configFile = new File( rootConfigDir, "JawaBotConfig-plugin-" + pluginID + ".xml" );
            if( ! configFile.exists() )
                throw new JawaBotException("Config file path for plugin '"+pluginID+"' not set in " + this.config.getReadFrom());
                //throw new JawaBotIOException("Default plugin config file not found: " + configFile.getAbsolutePath());
        }
        return configFile;
    }


}// class


