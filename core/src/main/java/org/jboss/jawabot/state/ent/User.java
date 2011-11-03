
package org.jboss.jawabot.state.ent;


import java.io.Serializable;
import java.util.*;
import java.util.logging.*;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 *
 * @author Ondrej Zizka
 */
@Entity
@Table(name = "jw_users")
@NamedQueries({
   @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
   @NamedQuery(name = "User.findByName", query = "SELECT u FROM User u WHERE u.name = :name")})
public class User implements Serializable
{
   private static final long serialVersionUID = 1L;
   @Id
   @Basic(optional = false)
   @Column(name = "name", nullable = false, length = 24)
   private String name;
   @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
   private Collection<UserInGroup> userInGroupCollection;
  private static final Logger log = Logger.getLogger( User.class.getName() );

   public User() {
   }

   public User(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Collection<UserInGroup> getUserInGroupCollection() {
      return userInGroupCollection;
   }

   public void setUserInGroupCollection(Collection<UserInGroup> userInGroupCollection) {
      this.userInGroupCollection = userInGroupCollection;
   }

   @Override
   public int hashCode() {
      int hash = 0;
      hash += (name != null ? name.hashCode() : 0);
      return hash;
   }

   @Override
   public boolean equals(Object object) {
      // TODO: Warning - this method won't work in the case the id fields are not set
      if (!(object instanceof User)) {
         return false;
      }
      User other = (User) object;
      if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name))) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "org.jboss.jawabot.state.ent.User[name=" + name + "]";
   }

}// class User
