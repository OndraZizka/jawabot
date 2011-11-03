
package org.jboss.jawabot.config.beans;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author Ondrej Zizka
 */
public class UserGroupBean implements Serializable {

   

   //@XmlElement
   @XmlValue
   @XmlList
   List<String> users;

}// class
