
package org.jboss.jawabot.config.beans;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.*;
import org.jboss.jawabot.Resource;

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

   //@XmlElement
   //public ResourcesBean resources;

   @XmlElementWrapper(name="resources")
   @XmlElement(name="resource")
   public List<Resource> resources;
   public List<Resource> getResources() {      return resources;   }


   @XmlElement
   public ResourceGroupsBean resourceGroups;
   
   @XmlElement
   public UserGroupsBean userGroups;



   // Catch-all HashMap. Is it needed?
   //@XmlTransient - ee... JAXB refuses this as not-a-property.
   /*
   public String getSetting( String name ){
      return this.settings.other.get(name);
   }

   public boolean getSettingBool( String name ){
      return "true".equalsIgnoreCase( this.settings.other.get(name) );
   }

   public int getSettingInt( String name ){
      return NumberUtils.toInt( this.settings.other.get(name) );
   }/**/
   
   
}// class
