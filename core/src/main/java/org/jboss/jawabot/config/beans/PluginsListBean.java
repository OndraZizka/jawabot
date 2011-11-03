package org.jboss.jawabot.config.beans;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *  Trying to map a map:
 * 
    <plugins>
      <plugin name="reservation" config="JawaBotConfig-plugin-reservation.xml"></plugin>
      <plugin name="jira"></plugin>
    </plugins>
 
 * 
 *  @author Ondrej Zizka
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "plugins")
public class PluginsListBean {
    
    @XmlElement(name = "plugin")
    private final List<PluginBean> pluginBeans = new ArrayList<PluginBean>();
    
    public List<PluginBean> getPluginBeans() {
        return this.pluginBeans;
    }
    
}// class
