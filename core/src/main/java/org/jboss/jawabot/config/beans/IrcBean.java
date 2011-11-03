
package org.jboss.jawabot.config.beans;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Ondrej Zizka
 */
public class IrcBean implements Serializable {

   @XmlElement public String defaultNick;

   @XmlElement(name="server") public List<ServerBean> servers;

}// class
