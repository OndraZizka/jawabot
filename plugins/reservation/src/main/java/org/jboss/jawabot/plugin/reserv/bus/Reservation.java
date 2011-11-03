package org.jboss.jawabot.plugin.reserv.bus;



import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.*;


/**
 * User forUser, Date from, Date to.
 * 
 * @author Ondrej Zizka
 */
//@Entity
//@XmlAccessorType(XmlAccessType.FIELD)
public class Reservation implements Comparable<Reservation>, Serializable  {


	/** Entity ID. */
   @XmlTransient
	@Id private Long id;
	public Long getId() {		return id;	}
	public void setId(Long id) {		this.id = id;	}


	/** Which user is this reservation for. */
	public String getForUser() {		return forUser;	}
   public void setForUser(String forUser) { this.forUser = forUser; }
   @XmlAttribute
	private String forUser;

	/** First day of the reservation. */
	@Temporal(javax.persistence.TemporalType.DATE)
	@Column(name="fromDate")
   public Date getFrom() {      return from;   }
   public void setFrom(Date from) { this.from = from; }
   @XmlAttribute
  	private Date from;



	/** Last day of the reservation. */
	@Temporal(javax.persistence.TemporalType.DATE)
	@Column(name="toDate")
	public Date getTo() {		return to;	}
   public void setTo(Date to) { this.to = to; }
   @XmlAttribute
	private Date to;


	public Reservation(String forUser, Date from, Date to) {
      if( from.after(to) )
         throw new IllegalArgumentException("End date precedes start date.");
		this.forUser = forUser;
		this.from = from;
		this.to = to;
	}

	/** For JPA */
	public Reservation() {	}


   

	/**
	 * Does the other reservation collide with this?
	 * @returns true if the other reservation collides, which means that they overlap somehow.
	 */
	public boolean collidesWith( Reservation other ){

		// This starts after other ends.
		if( this.from.after( other.to ))
			return false;

		// This ends before other starts.
		if( this.to.before( other.from ))
			return false;
		
		// All other cases overlap.
		return true;

	}


	/**
	 * Does given date belong into this period?
	 * @returns true if it does.
	 */
	public boolean contains( Date date ){
		// This starts after other ends.
		if( this.from.after( date ))
			return false;

		// This ends before other starts.
		if( this.to.before( date ))
			return false;

		// All other cases overlap.
		return true;
	}

   
   @Override
   public int compareTo( Reservation reservation ) {
		if( this.from.after( reservation.to ))
			return 1;
		if( this.to.before( reservation.from ))
			return -1;
		return 0; // Overlaps
   }


   @Override
   public String toString() {
      return String.format("%s %s %s", this.forUser, DateParser.format(this.from), DateParser.format(this.to) );
   }







	/**
	 * Parses one or two dates from the given string in the form "YYYY-MM-DD[ YYYY-MM-DD]".
	 * If the second is earlier than the first, they are swapped.
	 *
	 * @param paramsStr   string in the form "YYYY-MM-DD[ YYYY-MM-DD]"
	 * @returns A reservation object with dates filled with the dates from the string.
	 * @throws java.text.ParseException
	 */
	public static Reservation parseDates( String paramsStr ) throws ParseException {

         String parts[] = paramsStr.split(" ");

         String resName = parts[0];
         String dateFromStr = parts.length > 1 ? parts[1] : null;
         String dateToStr = parts.length > 2 ? parts[2] : null;


         // Parse the dates. TODO: Date parser.
         Date fromDate;
         try {
            fromDate = DateParser.parse(dateFromStr);
         } catch (ParseException ex) {
            throw new ParseException( "Invalid date: "+dateFromStr+"' Try YYYY-MM-DD.", 1);
         }

         Date toDate;
         try {
            toDate = DateParser.parse(dateToStr);
         } catch (ParseException ex) {
            throw new ParseException( "Invalid date: "+dateFromStr+"' Try YYYY-MM-DD.", 1);
         }

         // If fromDate is later than toDate, swap the dates.
         if( toDate.before(fromDate) ){
            Date tmp = toDate;
            toDate = fromDate;
            fromDate = tmp;
         }

         return new Reservation(null, fromDate, toDate);

	}



}// class 



