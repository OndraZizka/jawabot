
package org.jboss.jawabot.plugin.jira.scrapers;

import org.jboss.jawabot.plugin.jira.config.beans.RepositoryBean;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.math.NumberUtils;



/**
 * Responsible for getting an information from the given repo for the given issue ID.
 *
 * Test account: spam.trash@seznam.cz
 *
 * @author Ondrej Zizka
 */
public class Bugzilla34xScraper extends HtmlUnitAbstractScraper {

    private static final String[][] issueProperties = new String[][] {
       {"Title", "subtitle"},
       //{"Key", "xpath:form[@name='changeform']/input[@name='id']/@value"},
       // Or:  <title>Bug 602788 &ndash; ...
       // Or: <td id="title"> <p>Red Hat Bugzilla &ndash; Bug&nbsp;602788</p> </td>
       {"Key", "title"},
       //{"Type", "type-val"}, // Stupid Bugzilla knows only one type.
       {"Status", "bug_status"}, // select#bug_status
       {"Priority", "priority"}, // select#priority
       {"Severity", "bug_severity"}, // select#bug_severity
       {"Assignee", "bz_assignee_edit_container"}, // div#bz_assignee_edit_container
       //{"Reporter", "xpath:'td[b[normalize-space()='Reported']]/following-sibling::td[1]/span/a/span"}, // Stupid Bugzilla
       //{"Votes", "votes-val"},
       //{"Watchers", "watchers-val"}
    };

   @Override
   public IssueInfo scrapeIssueInfo( RepositoryBean repo, String bugzillaNumber ) throws ScrapingException {

			if( ! NumberUtils.isDigits(bugzillaNumber) ) {
				throw new IllegalArgumentException("Invalid Bugzilla ID - must be a number: " + bugzillaNumber);
			}

			// Create the URL to download the page from.
			String url = repo.getUrl();
			if( url == null ) {
				throw new ScrapingException("Unknown URL for bug " + bugzillaNumber);
			}
			url = url + bugzillaNumber;


			// Download the page.
			HtmlPage page = null;
			try {
				page = (HtmlPage) wc.getPage(url);
			} catch (Exception ex) {
				throw new ScrapingException( "Problem communicating with " + url + " for bug " + bugzillaNumber + ": " + ex );
			}

         // Bug 123456 - Blah blah blah
			String pageTitle = page.getTitleText();


			// Login Required.
			if (pageTitle.toUpperCase().contains("login required")) {
				// DON'T DO: Login and re-download. Said to be a security threat.
				throw new ScrapingException( "Bug " + bugzillaNumber + " is secured. I can't login to see this issue." );
			}
			// Does not exist.
			else if (pageTitle.toLowerCase().contains("invalid bug id")) {
				try {
					String hostName = new URL(url).getHost();
					throw new ScrapingException( "Bugzilla at " + hostName + " reports that " + bugzillaNumber + " does not exist." );
				} catch (MalformedURLException murle) {
					throw new ScrapingException( "Bugzilla at " + url + " reports that " + bugzillaNumber + " does not exist." );
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
         IssueInfo iinfo = new IssueInfo(bugzillaNumber, title, priority, assignee, status);
         return iinfo;

   }



   
   @Override
   public String createUrlForIssueID(RepositoryBean repo, String issueID) throws ScrapingException {
      return repo.getUrl() + issueID;
   }


}// class
