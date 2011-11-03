
package org.jboss.jawabot.state.ent;


import java.io.Serializable;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Ondrej Zizka
 */
@Entity
@Table(name = "jw_user_grp", uniqueConstraints = {
   @UniqueConstraint(columnNames = {"user", "grp"})})
@NamedQueries({
   @NamedQuery(name = "UserInGroup.findAll", query = "SELECT u FROM UserInGroup u"),
   @NamedQuery(name = "UserInGroup.findById", query = "SELECT u FROM UserInGroup u WHERE u.id = :id"),
   @NamedQuery(name = "UserInGroup.findByVer", query = "SELECT u FROM UserInGroup u WHERE u.ver = :ver")})
public class UserInGroup implements Serializable
{
   private static final long serialVersionUID = 1L;
   private static final Logger log = LoggerFactory.getLogger( UserInGroup.class.getName() );

   
   
   @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Basic(optional = false)
   @Column(name = "id", nullable = false)
   private Integer id;
   
   @Basic(optional = false)
   @Column(name = "ver", nullable = false)
   private int ver;
   
   @JoinColumn(name = "user", referencedColumnName = "name", nullable = false)
   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   private User user;
   
   @JoinColumn(name = "grp", referencedColumnName = "name", nullable = false)
   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   private Group group;

   
   
   public UserInGroup() {
   }

   public UserInGroup(Integer id) {
      this.id = id;
   }

   public UserInGroup(Integer id, int ver) {
      this.id = id;
      this.ver = ver;
   }

   
   
   public Integer getId() { return id; }
   public void setId(Integer id) { this.id = id; }
   public int getVer() { return ver; }
   public void setVer(int ver) { this.ver = ver; }
   public User getUser() { return user; }
   public void setUser(User user) { this.user = user; }
   @Column(name="grp")
   public Group getGroup() { return group; }
   public void setGroup(Group group) { this.group = group; }
   
   
   
   
   @Override
   public int hashCode() {
      int hash = 0;
      hash += (id != null ? id.hashCode() : 0);
      return hash;
   }

   @Override
   public boolean equals(Object object) {
      // TODO: Warning - this method won't work in the case the id fields are not set
      if (!(object instanceof UserInGroup)) {
         return false;
      }
      UserInGroup other = (UserInGroup) object;
      if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "org.jboss.jawabot.state.ent.UserInGroup[id=" + id + "]";
   }

}// class UserInGroup
