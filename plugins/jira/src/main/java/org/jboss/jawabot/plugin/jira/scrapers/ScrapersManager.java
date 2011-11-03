
package org.jboss.jawabot.plugin.jira.scrapers;

import org.apache.log4j.Logger;

/**
 *
 * @author Ondrej Zizka
 */
public class ScrapersManager
{
   private static final Logger log = Logger.getLogger( ScrapersManager.class );


    // -- SCRAPERS -- //
    private static final Jira3xScraper SCRAPER_JIRA3X = new Jira3xScraper();
    private static final Jira41Scraper SCRAPER_JIRA41 = new Jira41Scraper();
    private static final Bugzilla34xScraper SCRAPER_BUGZILLA34 = new Bugzilla34xScraper();

    /**
     * @returns Scraper object for the given repository type (jira3x, jira41, bugzilla34).
     */
    public static IssueScraper getScraperForRepoType( String typeName )
    {
       if( "jira3x".equals(typeName) )
          return SCRAPER_JIRA3X;

       if( "jira41".equals(typeName) )
          return SCRAPER_JIRA41;

       if( "bugzilla34".equals(typeName) )
          return SCRAPER_BUGZILLA34;

       log.error("  No suitable scraper for repository type: " + typeName);
       return null;
    }


}// class
