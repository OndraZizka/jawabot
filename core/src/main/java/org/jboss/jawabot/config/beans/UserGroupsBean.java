
package org.jboss.jawabot.config.beans;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Ondrej Zizka
 */
public class UserGroupsBean implements Serializable {

   /*
   <userGroups>
      <group name="soa">oskutka jharting</group>
      <group name="eap">ozizka rhusar</group>
   </userGroups>
   */

   @XmlAttribute
   String name;

   @XmlElement(name="group")
   List<UserGroupBean> groups;


}// class
