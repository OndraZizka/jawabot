package org.jboss.jawabot.plugin.reserv.config;


import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.*;
import org.jboss.jawabot.plugin.reserv.bus.Resource;
import org.jboss.jawabot.config.beans.UserGroupsBean;

    
/**
 *
 * @author Ondrej Zizka
 */
@XmlRootElement(name="jawabotConfig"
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


   @XmlElement
   public ResourceGroupsBean resourceGroups;

   // Plugin or core?
   @XmlElement
   public UserGroupsBean userGroups;

   
}// class
