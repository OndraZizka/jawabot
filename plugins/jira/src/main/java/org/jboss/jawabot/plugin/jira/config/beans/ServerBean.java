
package org.jboss.jawabot.plugin.jira.config.beans;

import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author Ondrej Zizka
 */
public class ServerBean {

   @XmlAttribute public String host;
   @XmlAttribute public Integer port;

   //@XmlElementWrapper(name="autoJoinChannels")
   @XmlElement
   @XmlList public List<String> autoJoinChannels;

}// class
