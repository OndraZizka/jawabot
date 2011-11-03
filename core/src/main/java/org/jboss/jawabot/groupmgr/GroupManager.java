
package org.jboss.jawabot.groupmgr;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ondra
 */
public class GroupManager {
   
   Map<String, Group> byName = new HashMap();

   public Collection<Group> values() {
      return byName.values();
   }

   public int groupsCount() {
      return byName.size();
   }

   public Group removeGroup(String key) {
      return byName.remove(key);
   }

   public Group addGroup(Group value) {
      return byName.put(value.getName(), value);
   }

   public Group getByName(String key) {
      return byName.get(key);
   }
   
}
