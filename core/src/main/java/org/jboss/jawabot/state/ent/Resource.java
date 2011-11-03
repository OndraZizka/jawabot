
package org.jboss.jawabot.state.ent;


import java.io.Serializable;
import java.util.*;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Ondrej Zizka
 */
@Entity
@Table(name = "jw_resources")
@NamedQueries({
   @NamedQuery(name = "Resource.findAll", query = "SELECT r FROM Resource r"),
   @NamedQuery(name = "Resource.findByName", query = "SELECT r FROM Resource r WHERE r.name = :name")})
public class Resource implements Serializable
{
   private static final long serialVersionUID = 1L;
   private static final Logger log = LoggerFactory.getLogger( Resource.class.getName() );
   
   
   @Id  @Basic(optional = false)
   @Column(name = "name", nullable = false, length = 8)
   private String name;
   
   
   @OneToMany(cascade = CascadeType.ALL, mappedBy = "res", fetch = FetchType.LAZY)
   private Collection<ResourceInGroup> resourceInGroupCollection;
   

   
   
   public Resource() {
   }

   public Resource(String name) {
      this.name = name;
   }

   
   
   public String getName() { return name; }
   public void setName(String name) { this.name = name; }
   
   
   public Collection<ResourceInGroup> getResourceInGroupCollection() {
      return resourceInGroupCollection;
   }

   public void setResourceInGroupCollection(Collection<ResourceInGroup> resourceInGroupCollection) {
      this.resourceInGroupCollection = resourceInGroupCollection;
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
      if (!(object instanceof Resource)) {
         return false;
      }
      Resource other = (Resource) object;
      if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name))) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "org.jboss.jawabot.state.ent.Resource[name=" + name + "]";
   }

}// class Resource
