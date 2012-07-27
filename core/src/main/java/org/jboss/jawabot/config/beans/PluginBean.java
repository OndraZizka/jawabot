package org.jboss.jawabot.config.beans;

import javax.xml.bind.annotation.XmlAttribute;

/**
 *  Plugin element.
 *  @author Ondrej Zizka
 */
public class PluginBean {
   
    @XmlAttribute
    public String id;

    @XmlAttribute
    public String configPath;

    
    
    public PluginBean() {
    }

    /** Used by PluginsMapAdaptor.marshall(). */
    public PluginBean( String id, String config ) {
        this.id = id;
        this.configPath = config;
    }

    
    
    public String getConfigPath() { return configPath; }
    public String getId() { return id; }
    
}// class

