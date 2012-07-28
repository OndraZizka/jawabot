
package org.jboss.jawabot.plugin.jira.config.beans;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import org.jboss.jawabot.plugin.jira.scrapers.IssueInfo;
import org.jboss.jawabot.plugin.jira.scrapers.IIssueScraper;
import org.jboss.jawabot.plugin.jira.scrapers.ScrapingException;

/**
 *
 * @author Ondrej Zizka
 */
public class RepositoryBean {

   private String name;
   @XmlAttribute public String getName() {      return name;   }
   public void setName(String name) {      this.name = name;   }

   private String url;
   @XmlAttribute
   public String getUrl() {      return url;   }
   public void setUrl(String url) {      this.url = url;   }

   private String type;
   @XmlAttribute
   public String getType() {      return type;   }
   public void setType(String type) {      this.type = type;   }
   

   @XmlElement(name="project") public List<ProjectBean> projects;
   @XmlTransient public List<ProjectBean> getProjects() {      return projects;   }
   public void setProjects(List<ProjectBean> projects) {      this.projects = projects;   }

   /**  Issue scraper for this repository. */
   private IIssueScraper scraper;
   @XmlTransient
   public IIssueScraper getScraper() {      return scraper;   }
   public void setScraper(IIssueScraper scraper) {      this.scraper = scraper;   }

   /** 
    * Convenience for repo.getScraper().scrapeIssueInfo(repo, issueID);
    * @see IssueScraper.scrapeIssueInfo(repo, issueID)
    */
   public IssueInfo scrapeIssueInfo( String issueID ) throws ScrapingException {
      if( this.getScraper() == null )
         throw new IllegalStateException("Scraper for repo not set: "+this);
      
      return this.getScraper().scrapeIssueInfo( this, issueID );
   }



   @Override
   public String toString() {
      return "Repo{ " + name + " " + url + " T: " + type + " prj: " + (projects == null ? "null" : projects.size()) + " scraper: " + scraper + '}';
   }



   

}// class
