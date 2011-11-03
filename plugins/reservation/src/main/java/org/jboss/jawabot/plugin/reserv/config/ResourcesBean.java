
package org.jboss.jawabot.plugin.reserv.config;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import org.jboss.jawabot.plugin.reserv.bus.Resource;

/**
 *
 * @author Ondrej Zizka
 */
public class ResourcesBean implements Serializable {

   /*
   <resources>
      <resource name="jawa01"/>
      <resource name="jawa01"/>
      <resource name="jawa02"/>
   </resources>
   */


   @XmlElement(name="resource")
   public List<Resource> resources;
   

}// class
