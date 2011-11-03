
package org.jboss.jawabot.plugin.jira.config.beans;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Ondrej Zizka
 */
public class IrcBean implements Serializable {

   @XmlElement public String nick;

   @XmlElement(name="server") public List<ServerBean> servers;

}// class
