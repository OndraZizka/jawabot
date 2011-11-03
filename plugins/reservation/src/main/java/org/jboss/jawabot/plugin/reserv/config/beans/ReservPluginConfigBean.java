package org.jboss.jawabot.plugin.reserv.config.beans;


import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.*;
import org.jboss.jawabot.config.beans.GenericGroupBean;
import org.jboss.jawabot.plugin.reserv.bus.Resource;

    
/**
 *
 * @author Ondrej Zizka
 */
@XmlRootElement(name="pluginConfig"
    //factoryClass=org.jboss.jawabot.config.ConfigBeanFactory.class
    //factoryMethod=""
)
public class ReservPluginConfigBean implements Serializable {


    @XmlElement
    public ReservPluginSettingsBean settings;

    @XmlElementWrapper(name="resources")
    @XmlElement(name="resource")
    public List<Resource> resources;
    public List<Resource> getResources() {      return resources;   }


    @XmlElementWrapper(name="resourceGroups")
    @XmlElement
    public List<GenericGroupBean> resourceGroups;
		public List<GenericGroupBean> getResourceGroups() {				return resourceGroups;		}
		

   
}// class
