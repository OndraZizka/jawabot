
package org.jboss.jawabot.plugin.jira.config.beans;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlList;

/**
 *
 * @author Ondrej Zizka
 */
public class JiraBean {

   /** Default repository for unmatched issue prefixes. */
   @XmlAttribute public String defaultRepo;

   /** Default repository type - if a repo has no type set. */
   @XmlAttribute public String defaultType;

   //@XmlElement(name="repository") public List<RepositoryBean> repositories;

   @XmlElementWrapper(name="repositories")
   @XmlElement(name="repository")
   public List<RepositoryBean> repositories;

   @XmlList public List<String> ignoredPrefixes;
   

}// class
