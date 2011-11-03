
package org.jboss.jawabot.config.beans;

import java.io.Serializable;
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

   @XmlElement
   public UserGroupsBean userGroups;
   
}// class
