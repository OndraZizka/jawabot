
package org.jboss.jawabot.plugin.jira.config.beans;

import javax.xml.bind.annotation.*;


/**
 *
 * @author Ondrej Zizka
 */
@XmlRootElement(name="jiraPluginConfig"
   //factoryClass=org.jboss.jawabot.config.ConfigBeanFactory.class
   //factoryMethod=""
)
public class JiraPluginConfigBean {

   @XmlElement
   public SettingsBean settings;

   @XmlElement
   public JiraBean jira;

   
}// class
