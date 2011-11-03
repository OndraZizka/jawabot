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
    public String config;

    
    
    public PluginBean() {
    }

    /** Used by PluginsMapAdaptor.marshall(). */
    public PluginBean( String id, String config ) {
        this.id = id;
        this.config = config;
    }

}// class

