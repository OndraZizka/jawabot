package org.jboss.jawabot.plugin.jira.core;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.jawabot.JawaBot;
import org.jboss.jawabot.config.JaxbGenericPersister;
import org.jboss.jawabot.ex.JawaBotException;
import org.jboss.jawabot.ex.JawaBotIOException;
import org.jboss.jawabot.plugin.IJaxbConfigurablePlugin;
import org.jboss.jawabot.plugin.jira.config.beans.JiraPluginConfigBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * In this class, I'm testing a new concept:
 * Some plugins have more complex configuration, which doesn't fit into the main configuration file.
 * Since JAXB isn't excensible top-down (children types are listed in parents),
 * I have to split that config to different file and read it into another JAXB tree.
 * 
 * This is intended to be a CDI Bean which will get JawaBot, get pluginName -> configFile map,
 * read & process the config and provide the config model.
 * 
 * @author Ondrej Zizka
 */
@ApplicationScoped
public class JiraPlugin implements IJaxbConfigurablePlugin<JiraPluginConfigBean> {
		
		private static final Logger log = LoggerFactory.getLogger( JiraPlugin.class );
		
		public static final String JIRA_PLUGIN_NAME = "jira";
		
		
		@Inject private JawaBot jawaBot;
		
		private JiraPluginConfigBean config;
		
		
		/**
		 *  Gets this plugin's config file path,
		 *  parses it via JAXB
		 *  and applies it to this plugin.
		 */
		@PostConstruct
		private void init() throws JawaBotIOException, JawaBotException{
				log.info("@PostConstruct init()");
				
				String configPath = jawaBot.getConfig().getPluginsMap().get( JIRA_PLUGIN_NAME );
				
        // Config
				// TODO: Move to JawaBotApp.readCustomConfig() ?  --> Done
        /*JaxbGenericPersister<JiraPluginConfigBean> persister = 
            new JaxbGenericPersister( configPath, JiraPluginConfigBean.class );
        JiraPluginConfigBean pluginConfigBean = persister.load();*/
				JiraPluginConfigBean pluginConfigBean = jawaBot.readCustomConfig( "jira", JiraPluginConfigBean.class );
				
				this.applyConfig( pluginConfigBean );
				
		}

		/**
		 * Applies (processes) the config bean to actual config model.
		 * @param pluginConfig
		 */
		@Override
		public void applyConfig(JiraPluginConfigBean pluginConfig) {
				this.config = pluginConfig;
		}

		@Override
		public Class getJaxbBeanClass() {
				return JiraPluginConfigBean.class;
		}

		
		public JiraPluginConfigBean getConfig() {
				return config;
		}
		
		
		
		
}// class

