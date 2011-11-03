
package org.jboss.jawabot.groupmgr;

import java.io.Serializable;

/**
 *
 * @author ondra
 */
public class Group implements Serializable {
   
   protected String name;
   public String getName() { return name; }
   public void setName(String name) { this.name = name; }
   
}
