
package org.jboss.jawabot.plugin.reserv.state.ent;


import java.io.Serializable;
import java.util.*;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.jboss.jawabot.state.ent.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 *
 * @author Ondrej Zizka
 */
@Entity
@Table(name = "jw_res_grp", uniqueConstraints = {
   @UniqueConstraint(columnNames = {"res", "grp"})})
@NamedQueries({
   @NamedQuery(name = "ResourceInGroup.findAll", query = "SELECT r FROM ResourceInGroup r"),
   @NamedQuery(name = "ResourceInGroup.findById", query = "SELECT r FROM ResourceInGroup r WHERE r.id = :id"),
   @NamedQuery(name = "ResourceInGroup.findByVer", query = "SELECT r FROM ResourceInGroup r WHERE r.ver = :ver")})
public class ResourceInGroup implements Serializable
{
   private static final long serialVersionUID = 1L;
   private static final Logger log = LoggerFactory.getLogger( ResourceInGroup.class.getName() );
   
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Basic(optional = false)
   @Column(name = "id", nullable = false)
   private Integer id;
   
   @Basic(optional = false)
   @Column(name = "ver", nullable = false)
   private int ver;
   
   @JoinColumn(name = "res", referencedColumnName = "name", nullable = false)
   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   private Resource res;
   
   @JoinColumn(name = "grp", referencedColumnName = "name", nullable = false)
   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   private Group group;

   
   
   public ResourceInGroup() {
   }

   public ResourceInGroup(Integer id) {
      this.id = id;
   }

   public ResourceInGroup(Integer id, int ver) {
      this.id = id;
      this.ver = ver;
   }

   
   
   public Integer getId() { return id; }
   public void setId(Integer id) { this.id = id; }
   public int getVer() { return ver; }
   public void setVer(int ver) { this.ver = ver; }
   public Resource getRes() { return res; }
   public void setRes(Resource res) { this.res = res; }
   public Group getGroup() { return group; }
   public void setGroup(Group grp) { this.group = grp; }
   
   
   
   
   
   @Override
   public int hashCode() {
      int hash = 0;
      hash += (id != null ? id.hashCode() : 0);
      return hash;
   }

   @Override
   public boolean equals(Object object) {
      // TODO: Warning - this method won't work in the case the id fields are not set
      if (!(object instanceof ResourceInGroup)) {
         return false;
      }
      ResourceInGroup other = (ResourceInGroup) object;
      if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "org.jboss.jawabot.state.ent.ResourceInGroup[id=" + id + "]";
   }

}// class ResourceInGroup
