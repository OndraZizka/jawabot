
package org.jboss.jawabot.plugin.jira.scrapers;

import org.jboss.jawabot.plugin.jira.config.beans.RepositoryBean;

/**
 * Responsible for getting an information from the given repo for the given issue ID.
 *
 * @author Ondrej Zizka
 */
public interface IssueScraper {

   /**
    *  Responsible for creating and populating the IssueInfo object.
    */
   public IssueInfo scrapeIssueInfo( RepositoryBean repo, String issueID ) throws ScrapingException;


   /**
    *  @returns the URL for the given JIRA id.
    *  @throws ScrapingException on error - e.g. unknown URL for that repo, or the issue ID does not belong to it.
    */
   public String createUrlForIssueID( RepositoryBean repo, String jiraID ) throws ScrapingException;

}// interface




