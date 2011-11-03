
package org.jboss.jawabot.plugin.reserv.config.beans;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author Ondrej Zizka
 * @deprecated - GenericGroupBean + @XmlElementWrapper(name="resourceGroups")
 */
public class ResourceGroupBean implements Serializable {

   @XmlAttribute
   String name;

   //@XmlElement // --> <group name="soa"><resources>jawa01 jawa02 jawa03</resources></group>
   @XmlValue     // --> <group name="soa">jawa01 jawa02 jawa03</group>
   @XmlList
   List<String> members;

}// class
