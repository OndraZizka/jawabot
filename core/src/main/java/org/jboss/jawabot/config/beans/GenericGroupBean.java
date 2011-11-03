
package org.jboss.jawabot.config.beans;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author Ondrej Zizka
 */
public class GenericGroupBean implements Serializable {

   @XmlAttribute
   String name;

   //@XmlElement // --> <group name="soa"><members>jawa01 jawa02 jawa03</members></group>
   @XmlValue     // --> <group name="soa">jawa01 jawa02 jawa03</group>
   @XmlList
   List<String> members;

}// class
