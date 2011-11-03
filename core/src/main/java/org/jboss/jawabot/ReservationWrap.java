
package org.jboss.jawabot;

import java.util.Date;
import java.io.Serializable;


/**
 * Adds a type to the Reservation:
 *
 *   public enum Type { NORMAL, COLLISION, MERGED }
 * 
 * NORMAL - normal reservation.
 * COLLISION - information about colliding existing reservation.
 * MERGED - reservation was merged with existing reservation of the same user.
 * 
 * @author Ondrej Zizka
 */
public class ReservationWrap /*extends Reservation*/ implements Serializable {

   
   public enum Type { NORMAL, COLLISION, MERGED }

   public final Type type;
   public Type getType() {      return type;   }

   public String resourceName;
   public String getResourceName() {      return resourceName;   }
   public void setResourceName(String resourceName) {      this.resourceName = resourceName;   }

   public final Reservation resv;


   // Const

   public ReservationWrap(String forUser, Date from, Date to, Type type) {
      this.resv = new Reservation(forUser, from, to);
      this.type = type;
   }

   public ReservationWrap(Type type, Reservation resv) {
      this.type = type;
      this.resv = resv;
   }

   public ReservationWrap(Type type, Reservation resv, String resourceName) {
      this.type = type;
      this.resourceName = resourceName;
      this.resv = resv;
   }




   /** toString() */
   public String toString() {
      return this.type.name() +" "+ resv.toString();
   }


   // Delegates
   public Date getTo() {      return resv.getTo();   }
   public void setTo(Date to) { resv.setTo(to); }
   public Date getFrom() {      return resv.getFrom();   }
   public void setFrom(Date from) { resv.setFrom(from); }
   public String getForUser() {      return resv.getForUser();   }
   public void setForUser(String forUser) { resv.setForUser(forUser); }
      
   
   public boolean contains(Date date) {      return resv.contains(date);   }
   public int compareTo(Reservation reservation) {      return resv.compareTo(reservation);   }
   public boolean collidesWith(Reservation other) {      return resv.collidesWith(other);   }








}// class
