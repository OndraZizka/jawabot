package org.jboss.jawabot.plugin.reserv.bus;



import cz.dynawest.util.DateUtils;
import java.io.Serializable;
import java.util.*;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Wrapper - basically, an ArrayList of reservations.
 * @author Ondrej Zizka
 */
public class ReservationCalendar implements Serializable
{
   private static final Logger log = LoggerFactory.getLogger( ReservationCalendar.class );


   
   @XmlElement(name="reservation")
   public List<Reservation> getReservations() { return reservations; }
   private List<Reservation> reservations = new ArrayList();




	/**
	 * @returns a reservation which contains current date-time.
	 */
	public Reservation getCurrentReservation() {

		Date now = DateUtils.truncate( new Date(), Calendar.DATE );
		
		for (Reservation reservation : reservations) {
			if( reservation.contains( now )){
				return reservation;
			}
		}
		return null;
	}


   /**
    * @returns  The nearest reservation from today (incl. current one),
    *           or null if no reservations.
    */
   public Reservation getNearestFutureReservation(){

      List<Reservation> resvs =  this.reservations;
		if( resvs.size() == 0 )  return null;
      if( resvs.size() == 1 )  return resvs.get(0);
      Reservation resv_ = getCurrentReservation();
      if( null != resv_ )  return resv_;

      //Date first = null, last = null;
      Reservation resvEarliest = resvs.get(0);
      Date NOW = new Date();

      for( Reservation resv : resvs ) {
         // Skip past reservations.
         if( resv.getFrom().before( NOW ) && resv.getTo().before( NOW ) )
            continue;

         if( resv.getFrom().before( resvEarliest.getFrom() ) ){
            resvEarliest = resv;
         }
      }
      return resvEarliest;
   }



	/**
	 *
	 * @return
	 */
	public String printReservations() {
		StringBuilder sb = new StringBuilder();
		for (Reservation res : this.reservations) {
			sb.append( String.format("%s - %s : %s",
							DateParser.format( res.getFrom() ),
							DateParser.format( res.getTo() ),
							res.getForUser()
			));
			sb.append("\n");
		}
		return sb.toString();
	}



   /**
    * Iterates through the reservations and purges those which ended while ago.
    */
	void purgeOldReservations() {
      //Date whileAgo = DateUtils.truncate( new Date(), Calendar.DATE );
      Date whileAgo = DateUtils.addHours( new Date(), -24 );

		Iterator<Reservation> iterator = reservations.iterator();
		while( iterator.hasNext() ){
			Reservation res = iterator.next();
			if( whileAgo.after( res.getTo() ) ){
				// Old, remove.
				iterator.remove();
			}
		}

	}


	/**
    * Returns the first reservation that collides with the given one,
    * including those owned by the owner of the given reservation.
	 */
	Reservation findCollidingReservation( Reservation other ){
      return findCollidingReservation(other, false);
   }


   /**
    * Returns the first reservation that collides with the given one.
    * 
    * @param onlyOtherUsers  If true, skips the reservations of the user owning the given reservation.
    */
   Reservation findCollidingReservation( Reservation other, boolean onlyOtherUsers )
	{
		for (Reservation reservation : reservations) {
         // The same owner and we look for other users' reservations.
         if( onlyOtherUsers && StringUtils.equals( reservation.getForUser(), other.getForUser() ) )
            continue;

			if( reservation.collidesWith( other )){
				return reservation;
			}
		}
		return null;
	}


   /**
    * Returns true if there's no reservation overlapping the given period.
    */
	public boolean isFreeForPeriod( Date fromDate, Date toDate ){
		return null == findCollidingReservation( new Reservation(null, fromDate, toDate));
	}


	/**
	 * Creates a reservation and returns null, or in case of collision, returns the colliding reservation.
	 * @param forUser
	 * @param from
	 * @param to
	 * @return
	 */
	public Reservation bookReservation( String forUser, Date from, Date to ){

		Reservation newRes = new Reservation(forUser, from, to);
		Reservation colliding = findCollidingReservation(newRes);
		if( null != colliding ){
			return colliding;
		}

		this.reservations.add(newRes);
		return null;
		
	}


   /** Creates a reservation.
    * @returns Reservation wrapper with information about either colliding or created reservation.
    */
	public ReservationWrap bookReservationWrap( String forUser, Date from, Date to ){

		Reservation newRes = new Reservation(forUser, from, to);
		Reservation colliding = findCollidingReservation(newRes);

      // Collision.
		if( null != colliding ){
			return new ReservationWrap(ReservationWrap.Type.COLLISION, colliding);
		}

      // Everything OK.
		this.reservations.add(newRes);
		return new ReservationWrap(ReservationWrap.Type.NORMAL, newRes);

	}


   /**
    *  Copied from ResourceManager and refactored.
    */
   public ReservationWrap bookReservationWrapMergeOwner( String forUser, Date fromDate, Date toDate ){

      final int MERGE_RETRIES = 5;

      Reservation newRes = new Reservation(forUser, fromDate, toDate);
      log.debug("Booking reservation: "+newRes);///

      // Try to resolve colision with self 5-times.
      for( int i = 0; i < MERGE_RETRIES; i++ ) {


         Reservation collidingRes = this.findCollidingReservation(newRes);

         // No collision, add the reservation to this calendar and return it.
         if( null == collidingRes ){
            this.reservations.add( newRes );
            return new ReservationWrap( i == 0 ? ReservationWrap.Type.NORMAL : ReservationWrap.Type.MERGED, newRes);
         }

         log.debug("Collision with: "+collidingRes);///

         // Other user - COLLISION.
         if( ! forUser.equals( collidingRes.getForUser() ) )
            return new ReservationWrap(ReservationWrap.Type.COLLISION, newRes);
         
         // The same user - MERGE.

         // Too many tries to merge - return as a collision (bug-caused infinite loop prevention).
         if( i >= MERGE_RETRIES -1 ){
            log.error("Maximum merge retries reached!");
            return new ReservationWrap(ReservationWrap.Type.COLLISION, newRes);
         }

         //fromDate = DateUtils.getEarlier(fromDate, collidingRes.getFrom());
         //toDate = DateUtils.getLater(toDate, collidingRes.getTo());
         newRes = ReservationCalendar.mergeReservations( forUser, newRes, collidingRes );
         this.remove(collidingRes);
         log.debug("Removed colliding resv, will try with the merged: "+newRes);

         // ... and create new merged reservation in next turn.
      }

      // This should never happen.
      throw new IllegalStateException("Skipped out of the loop! That means that JawaBot is poorly coded software :)");
      //return null;

   }





	/** Removes the given reservation from this calendar. */
	public boolean remove(Reservation curRes) {
		return this.reservations.remove(curRes);
	}


	
	private void cleanOldReservations(){

		Date yesterday = new Date( System.currentTimeMillis() - 24 * 60 * 60 * 1000 );

		for (Reservation reservation : reservations) {
			if( reservation.getTo().before( yesterday ) );
		}
	}




   private static Reservation mergeReservations( String forUser, Reservation a, Reservation b ) {
      Date fromDate = DateUtils.getEarlier( a.getFrom(), b.getFrom() );
      Date toDate = DateUtils.getLater( a.getTo(), b.getTo() );
      return new Reservation( forUser, fromDate, toDate);
   }




   public String toString(){
      return "Cal["+this.reservations.size()+" resv]";
   }


   // For @Entity.
   @XmlTransient
   @Id private Long id;
   public Long getId() {      return id;   }
   public void setId(Long id) {      this.id = id;   }
   

}// class
