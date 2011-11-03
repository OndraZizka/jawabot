
package org.jboss.jawabot.plugin.jira.scrapers;

import org.jboss.jawabot.plugin.jira.config.beans.RepositoryBean;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;



/**
 *
 * @author Ondrej Zizka
 */
public class Jira3xScraper extends HtmlUnitAbstractScraper {


   private static final String[] issueProperties = new String[] {"Key", "Type", "Status", "Priority", "Assignee", "Reporter", "Votes", "Watchers"};
   private static final String cellXpathPattern = ".//td[b[normalize-space()='%s:']]/following-sibling::td[1]";



   @Override
   public IssueInfo scrapeIssueInfo( RepositoryBean repo, String issueID ) throws ScrapingException {

      // TODO: Refactorize RepositoryParser.getJiraInfo()

      IssueInfo issue = null;
      String url = null;
      String errorReply = "";

      // JIRA-123  =>  [0] "JIRA", [1] "123"
      String[] parts = issueID.split("-");
      if (parts.length != 2) {
         // this should never happen since jiraID was implicitly validated by the JIRA_KEY_PATTERN
         throw new IllegalArgumentException("Invalid jira ID: " + issueID);
      }

      // Create the URL to download the page from.
      // TODO: repo.createUrlForIssueID()
      url = this.createUrlForIssueID( repo, issueID );


      // Download the page.
      HtmlPage page = null;
      try {
         page = (HtmlPage) wc.getPage(url);
      } catch (Exception ex) {
         errorReply = "Problem communicating with " + url + " for " + issueID + ": " + ex;
         throw new ScrapingException(errorReply);
      }

      String title = page.getTitleText();
      // TODO: Generalize
      title = title.replace(" - RHQ Project JIRA", "");
      title = title.replace(" - jboss.org JIRA", "");
      title = title.replace(" - JBoss Issue Tracker", "");
      title = title.replace(" - ASF JIRA", "");

      // Login Required.
      if( title.toUpperCase().contains("LOGIN REQUIRED") ) {
         // TODO: Login and re-download.
         errorReply = "JIRA issue " + issueID + " is secured. I can't login to see this issue.";
      }
      // Does not exist.
      else if( title.toUpperCase().contains("ISSUE DOES NOT EXIST") ) {
         try {
            String hostName = new URL(url).getHost();
            errorReply = "JIRA at " + hostName + " reports that " + issueID + " does not exist.";
         } catch (MalformedURLException murle) {
            errorReply = "Bad URL for " + issueID + ": " + url;
         }
      }
      // Issue found, read the details.
      else do {
         errorReply = title;
         if( !title.toUpperCase().startsWith("[#" + issueID + "]") )
            break;

         HtmlElement infoTable = page.getElementById("issuedetails");
         if (infoTable == null)
            break;

         Map<String, String> details = new HashMap<String, String>();
         for (String key : issueProperties) {
            String xPath = String.format(cellXpathPattern, key);
            HtmlTableCell td = infoTable.getFirstByXPath(xPath);
            if (null == td) {
               continue;
            }
            details.put(key, td.getTextContent().trim());
         }
         String status = details.get("Status");
         String priority = details.get("Priority");
         String assignee = details.get("Assignee");
         //errorReply += String.format(" [%s, %s, %s]", status, priority, assignee);

         if( status == null || priority == null || assignee == null )
         {
            // <span id="footer-build-information"  style="color: #666666;" >(v4.2.2-b589#589)</span>
            //String xPath = "//span[@id='footer-build-information']";
            String xPath = "//div[@class='poweredbymessage']";
            HtmlElement elm = infoTable.getFirstByXPath(xPath);
            String version = StringUtils.defaultString( elm.getTextContent() ).trim().replace("\n", " ").replace("| Report a problem", "");

            String msg = "Error scraping '"+issueID+"' from "+repo.getName()+" - maybe unexpected version? ";
            msg += String.format("Scraper: %s  Scraped: %s", this.getClass().getSimpleName(), version);
            throw new ScrapingException(msg);
            // LATER:  Rather try to detect the version by other scrapers. Maybe.
         }

         // Finally - create and return the Jira info object.
         issue = new IssueInfo( issueID, StringUtils.substringAfter(title, "]").trim(), priority, assignee, status );
         issue.setUrl( url );
         return issue;


      } while(false); // for breaks.


      throw new ScrapingException( errorReply );


   }

   public String createUrlForIssueID(RepositoryBean repo, String issueID) {
      return repo.getUrl() + issueID;
   }


}// class
