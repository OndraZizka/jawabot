
package org.jboss.jawabot.plugin.jira.scrapers;

import java.util.Map;



/**
 * Responsible for getting an information from the given repo for the given issue ID.
 *
 * @author Ondrej Zizka
 */
public class Jira50Scraper extends Jira41Scraper {

    private static final Map<String, String> ISSUE_PROPERTY_IDS = new Jira41Scraper().getIssuePropertyIDs();
    {
        ISSUE_PROPERTY_IDS.put( "Title", "summary-val");
    }

    protected Map<String,String> getIssuePropertyIDs(){
        return ISSUE_PROPERTY_IDS;
    }

}