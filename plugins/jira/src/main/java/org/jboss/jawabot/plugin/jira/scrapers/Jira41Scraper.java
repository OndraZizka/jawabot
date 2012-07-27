
package org.jboss.jawabot.plugin.jira.scrapers;

import org.jboss.jawabot.plugin.jira.config.beans.RepositoryBean;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;



/**
 * Responsible for getting an information from the given repo for the given issue ID.
 *
 * @author Ondrej Zizka
 */
public class Jira41Scraper extends HtmlUnitAbstractScraper {

    private static final String[][] issueProperties = new String[][] {
       {"Title", "issue_header_summary"},
       {"Key", "key-val"},
       {"Type", "type-val"},
       {"Status", "status-val"},
       {"Priority", "priority-val"},
       {"Assignee", "assignee-val"},
       {"Reporter", "reporter-val"},
       {"Votes", "votes-val"},
       {"Watchers", "watchers-val"}
    };

   @Override
   public IssueInfo scrapeIssueInfo( RepositoryBean repo, String jiraID ) throws ScrapingException {

        String[] parts = jiraID.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid Jira ID: " + jiraID);
        }

        // Create the URL to download the page from.
        String url = this.createUrlForIssueID( repo, jiraID );


        // Download the page.
        HtmlPage page = null;
        try {
            page = (HtmlPage) wc.getPage(url);
        } catch (Exception ex) {
            throw new ScrapingException( "Problem communicating with " + url + " for " + jiraID + ": " + ex );
        }

        String pageTitle = page.getTitleText();
        // TODO: Something smarter.
        pageTitle = pageTitle.replace(" - RHQ Project JIRA", "");
        pageTitle = pageTitle.replace(" - jboss.org JIRA", "");
        pageTitle = pageTitle.replace(" - JBoss Issue Tracker", "");


        // Login Required.
        if (pageTitle.toUpperCase().contains("LOGIN REQUIRED")) {
            // DON'T DO: Login and re-download. Said to be a security threat.
            throw new ScrapingException( "JIRA issue " + jiraID + " is secured. I can't login to see this issue." );
        }
        // Does not exist.
        else if (pageTitle.toUpperCase().contains("ISSUE DOES NOT EXIST")) {
            try {
                String hostName = new URL(url).getHost();
                throw new ScrapingException( "Jira at " + hostName + " reports that " + jiraID + " does not exist." );
            } catch (MalformedURLException murle) {
                throw new ScrapingException( "Jira at " + url + " reports that " + jiraID + " does not exist." );
            }
        }


        // --- Issue found, read the details. --- //

        //if( !pageTitle.toUpperCase().startsWith("[#" + jiraID + "]") )
        //   throw new ScrapingException("Weird page title: "+pageTitle);


        Map<String, String> details = new HashMap<String, String>();
        for( String[] propPair : issueProperties ) {
            //String xPath = String.format(CELL_XPATH_PATTERN, propPair[1]);
            //HtmlTableCell td = primaryDiv.getFirstByXPath(xPath);
            HtmlElement valElm = page.getElementById(propPair[1]);
            if (null == valElm) {
                continue;
            }
            details.put(propPair[0], valElm.getTextContent().trim());
        }

         // Finally - create and return the Jira info object.
         String title = details.get("Title");
         String status = details.get("Status");
         String priority = details.get("Priority");
         String assignee = details.get("Assignee");
         IssueInfo iinfo = new IssueInfo(jiraID, title, priority, assignee, status);
         iinfo.setUrl( url );
         return iinfo;

   }


    /**
     *  @returns the URL for the given JIRA id.
     *  @throws ScrapingException on error - e.g. unknown URL for that repo, or the issue ID does not belong to it.
     */
    public String createUrlForIssueID( RepositoryBean repo, String jiraID ) throws ScrapingException {
        String url = repo.getUrl();
        if( url == null ) {
            throw new ScrapingException("Unknown URL for issue's repo: " + jiraID);
        }
        return url + jiraID;
    }


}// class
