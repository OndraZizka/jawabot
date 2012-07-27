
package org.jboss.jawabot.config.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.*;


/**
 *
 * @author Ondrej Zizka
 */
@XmlRootElement(name="jawabotConfig"
    //factoryClass=org.jboss.jawabot.config.ConfigBeanFactory.class
    //factoryMethod=""
)
public class ConfigBean implements Serializable {

    @XmlElement
    public SettingsBean settings;

    @XmlElement
    public IrcBean irc;
   
   
    // Bare beans, no map.
    @XmlElementWrapper( name="plugins" )
    @XmlElement( name="plugin" )
    public List<PluginBean> plugins;
    /**/
   
    // Or, as per http://www.mail-archive.com/cxf-user@incubator.apache.org/msg04723.html :
    // Or here: http://stackoverflow.com/questions/3941479/jaxb-how-to-marshall-map-into-keyvalue-key
    //@XmlElement( name = "plugins" )
    //@XmlJavaTypeAdapter( PluginsMapAdaptor.class )
    private Map<String, PluginBean> pluginsMap = new HashMap();
    public Map<String, PluginBean> getPluginsMap() {
        return this.pluginsMap;
    }/**/


   
    @XmlElement
    public UserGroupsBean userGroups;

    
    /**
     *  Goes through the list of plugins and creates a map ID -> PluginBean.
     */
    public void recreatePluginsMap() {
        this.pluginsMap = new HashMap<String, PluginBean>();
        for( PluginBean plugin : this.plugins ) {
            this.pluginsMap.put( plugin.id, plugin );
        }
    }
   
}// class
