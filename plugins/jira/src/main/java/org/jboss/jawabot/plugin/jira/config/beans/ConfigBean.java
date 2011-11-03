
package org.jboss.jawabot.plugin.jira.config.beans;

import javax.xml.bind.annotation.*;


/**
 *
 * @author Ondrej Zizka
 */
@XmlRootElement(name="jirabotConfig"
   //factoryClass=org.jboss.jawabot.config.ConfigBeanFactory.class
   //factoryMethod=""
)
public class ConfigBean {

   @XmlElement
   public SettingsBean settings;

   @XmlElement
   public IrcBean irc;

   @XmlElement
   public JiraBean jira;

   
}// class
