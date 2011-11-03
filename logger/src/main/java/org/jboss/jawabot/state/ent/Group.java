
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
@Table(name = "jw_groups")
@NamedQueries({
   @NamedQuery(name = "Grp.findAll", query = "SELECT g FROM Grp g"),
   @NamedQuery(name = "Grp.findByName", query = "SELECT g FROM Grp g WHERE g.name = :name")})
public class Group implements Serializable, Comparable<Group> 
{
   private static final long serialVersionUID = 1L;
   private static final Logger log = Logger.getLogger( Group.class.getName() );

   
   @Id
   @Basic(optional = false)
   @Column(name = "name", nullable = false, length = 16)
   private String name;
   
   @OneToMany(cascade = CascadeType.ALL, mappedBy = "group1", fetch = FetchType.LAZY)
   private Collection<UserInGroup> userInGroupCollection;
   
   @OneToMany(cascade = CascadeType.ALL, mappedBy = "grp", fetch = FetchType.LAZY)
   private Collection<ResourceInGroup> resourceInGroupCollection;
   

   public Group() {
   }

   public Group(String name) {
      this.name = name;
   }

   
   
   
   // Get / set
   
   public String getName() { return name; }
   public void setName(String name) { this.name = name; }
   
   public Collection<UserInGroup> getUserInGroupCollection() { return userInGroupCollection; }
   public void setUserInGroupCollection(Collection<UserInGroup> userInGroupCollection) { this.userInGroupCollection = userInGroupCollection; }
   
   public Collection<ResourceInGroup> getResourceInGroupCollection() { return resourceInGroupCollection; }
   public void setResourceInGroupCollection(Collection<ResourceInGroup> resourceInGroupCollection) { this.resourceInGroupCollection = resourceInGroupCollection; }
   
   
   
   
   
   // Overrides
   
   @Override
      public int compareTo(Group o) {
      if( o == null || o.getName() == null ) return -1;
      if( this.getName() == null ) return 1;
      return this.getName().compareTo( o.getName() );
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
      if (!(object instanceof Group)) {
         return false;
      }
      Group other = (Group) object;
      if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name))) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "org.jboss.jawabot.state.ent.Grp[name=" + name + "]";
   }

}// class Group
