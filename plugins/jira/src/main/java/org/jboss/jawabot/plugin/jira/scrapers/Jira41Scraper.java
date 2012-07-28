
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

    /**
     *  IDs of HTML elements containing parts of jira info.
     */
    private static final Map<String, String> ISSUE_PROPERTY_IDS = new HashMap();
    {
        ISSUE_PROPERTY_IDS.put( "Title", "issue_header_summary");
        ISSUE_PROPERTY_IDS.put( "Key", "key-val");
        ISSUE_PROPERTY_IDS.put( "Type", "type-val");
        ISSUE_PROPERTY_IDS.put( "Status", "status-val");
        ISSUE_PROPERTY_IDS.put( "Priority", "priority-val");
        ISSUE_PROPERTY_IDS.put( "Assignee", "assignee-val");
        ISSUE_PROPERTY_IDS.put( "Reporter", "reporter-val");
        ISSUE_PROPERTY_IDS.put( "Votes", "votes-val");
        ISSUE_PROPERTY_IDS.put( "Watchers", "watchers-val");
    }

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

        /*String pageTitle = page.getTitleText();
        // TODO: Something smarter.
        pageTitle = pageTitle.replace(" - RHQ Project JIRA", "");
        pageTitle = pageTitle.replace(" - jboss.org JIRA", "");
        pageTitle = pageTitle.replace(" - JBoss Issue Tracker", ""); /**/


        // Login Required.
        if( this.isLoginPage( page ) ) {
            // DON'T DO: Login and re-download. Said to be a security threat.
            throw new ScrapingException( "Issue " + jiraID + " is secured. I can't login. " + url );
        }
        // Does not exist.
        else if ( this.isNonExistentIssue(page) ) {
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

        for( Map.Entry<String,String> propPair : ISSUE_PROPERTY_IDS.entrySet() ) {
            HtmlElement valElm = page.getElementById( propPair.getValue() );
            if (null == valElm)  continue;
            details.put( propPair.getKey(), valElm.getTextContent().trim() );
        }
        
        // Finally - create and return the Jira info object.
        String title = this.getTitle( page, details );
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

    
    
    /**
     *  IDs of HTML elements containing parts of jira info.
     */
    protected Map<String,String> getIssuePropertyIDs(){
        return ISSUE_PROPERTY_IDS;
    }
    
    protected boolean isLoginPage( HtmlPage page ){
        return page.getTitleText().toUpperCase().contains("LOGIN REQUIRED");
    }
    
    protected String getTitle( HtmlPage page, Map<String, String> details ) {
        return details.get("Title");
    }

    protected boolean isNonExistentIssue( HtmlPage page ) {
        return page.getTitleText().toUpperCase().contains("ISSUE DOES NOT EXIST");
    }


}// class
