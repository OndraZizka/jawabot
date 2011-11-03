
package org.jboss.jawabot.plugin.reserv.bus;

import java.io.Serializable;

/**
 * DTO for displaying a resource reservation links - Today, Tomorrow, +2, ...
 * @author Ondrej Zizka
 */
public class ResourceWithNearestFreePeriodDTO implements Serializable {

   private Resource resource;
   private int freeSince;
   private int freeUntil;

   public ResourceWithNearestFreePeriodDTO(Resource resource, int freeSince, int freeUntil) {
      this.resource = resource;
      this.freeSince = freeSince;
      this.freeUntil = freeUntil;
   }

   public int getFreeSinceDay() { return freeSince; }
   public void setFreeSinceDay(int freeSinceDay) { this.freeSince = freeSinceDay; }
   public int getFreeUntil() { return freeUntil; }
   public void setFreeUntil(int freeUntil) { this.freeUntil = freeUntil; }
   public Resource getResource() { return resource; }
   public void setResource(Resource resource) { this.resource = resource; }

   public boolean isFreeToday(){ return freeSince == 0; }
   public boolean isFreeTodayPlusX( int x ){ return freeSince == 0 && ( freeUntil == -1 || freeUntil >= x ); }

}
