
package org.jboss.jawabot.config.beans;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author Ondrej Zizka
 */
public class ServerBean implements Serializable {

   @XmlAttribute public String host;
   @XmlAttribute public Integer port;

   //@XmlElementWrapper(name="autoJoinChannels")
   @XmlElement
   @XmlList public List<String> autoJoinChannels;

}// class
