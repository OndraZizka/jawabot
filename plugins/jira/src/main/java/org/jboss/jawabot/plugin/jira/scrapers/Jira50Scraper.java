
package org.jboss.jawabot.plugin.jira.scrapers;

import java.util.Map;
import org.apache.commons.lang.StringUtils;



/**
 * Responsible for getting an information from the given repo for the given issue ID.
 *
 * @author Ondrej Zizka
 */
public class Jira50Scraper extends Jira41Scraper {

    @Override
    protected String getTitle( String pageTitle, Map<String, String> details ) {
        String title = StringUtils.substringAfter( pageTitle, "]" ).trim();
        return title;
    }

}