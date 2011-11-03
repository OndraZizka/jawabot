
package org.jboss.jawabot.state.ent;


import java.io.Serializable;
import java.util.*;
import java.util.logging.*;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 *
 * @author Ondrej Zizka
 */
@Entity(name="Grp") // Group is a reserved JPA QL name.
@Table(name = "jw_reserv")
@NamedQueries({
   @NamedQuery(name = "Reservation.findAll", query = "SELECT r FROM Reservation r"),
   @NamedQuery(name = "Reservation.findById", query = "SELECT r FROM Reservation r WHERE r.id = :id"),
   @NamedQuery(name = "Reservation.findByRes", query = "SELECT r FROM Reservation r WHERE r.res = :res"),
   @NamedQuery(name = "Reservation.findBySince", query = "SELECT r FROM Reservation r WHERE r.since = :since"),
   @NamedQuery(name = "Reservation.findByUntil", query = "SELECT r FROM Reservation r WHERE r.until = :until")})
public class Reservation implements Serializable
{
   private static final long serialVersionUID = 1L;
   private static final Logger log = Logger.getLogger( Reservation.class.getName() );

   

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Basic(optional = false)
   @Column(name = "id", nullable = false)
   private Integer id;

   @Basic(optional = false)
   @Column(name = "res", nullable = false, length = 8)
   private String res;

   @Basic(optional = false)
   @Column(name = "since", nullable = false)
   @Temporal(TemporalType.TIMESTAMP)
   private Date since;

   @Basic(optional = false)
   @Column(name = "until", nullable = false)
   @Temporal(TemporalType.TIMESTAMP)
   private Date until;

   

   public Reservation() {
   }

   public Reservation(Integer id) {
      this.id = id;
   }

   public Reservation(Integer id, String res, Date from, Date to) {
      this.id = id;
      this.res = res;
      this.since = from;
      this.until = to;
   }

   public Integer getId() {      return id;   }
   public void setId(Integer id) {      this.id = id;   }
   public String getRes() {      return res;   }
   public void setRes(String res) {      this.res = res;   }
   public Date getFrom() {      return since;   }
   public void setFrom(Date from) {      this.since = from;   }
   public Date getTo() {      return until;   }
   public void setTo(Date to) {      this.until = to;   }


   
   @Override
   public int hashCode() {
      int hash = 0;
      hash += (id != null ? id.hashCode() : 0);
      return hash;
   }

   @Override
   public boolean equals(Object object) {
      // TODO: Warning - this method won't work in the case the id fields are not set
      if (!(object instanceof Reservation)) {
         return false;
      }
      Reservation other = (Reservation) object;
      if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "org.jboss.jawabot.state.ent.Reservation[id=" + id + "]";
   }

}// class Reservation
