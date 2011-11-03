package org.jboss.jawabot.config.beans;

import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *  Not used.
 * 
 *  @author Ondrej Zizka
 */
public class PluginsMapEntryType {

    @XmlAttribute // @XmlElement and @XmlValue are also fine
    public String id; 
    
    @XmlAttribute // @XmlElement and @XmlValue are also fine
    public int config;
    
    public PluginsMapEntryType() {}
    public PluginsMapEntryType( Map.Entry<String,Integer> e) {
       id = e.getKey();
       config = e.getValue();
    }
    
}// class
