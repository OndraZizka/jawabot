

package org.jboss.jawabot.config.beans;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Ondrej Zizka
 */
public class ResourceGroupsBean implements Serializable {

   /*
   <resourceGroups>
      <group name="soa">jawa01 jawa02 jawa03</group>
      <group name="embjopr">jawa18</group>
   </resourceGroups>
   */

   @XmlElement(name="group")
   List<ResourceGroupBean> groups;

}// class
