
package org.jboss.jawabot.plugin.jira.scrapers;

import org.slf4j.Logger; import org.slf4j.LoggerFactory; 

/**
 *
 * @author Ondrej Zizka
 */
public class ScrapersManager
{
   private static final Logger log = LoggerFactory.getLogger( ScrapersManager.class );


    // -- SCRAPERS -- //
    private static final Jira3xScraper SCRAPER_JIRA3X = new Jira3xScraper();
    private static final Jira41Scraper SCRAPER_JIRA41 = new Jira41Scraper();
    private static final Jira50Scraper SCRAPER_JIRA50 = new Jira50Scraper();
    private static final Jira50Scraper_JBoss SCRAPER_JIRA50_JBOSS = new Jira50Scraper_JBoss();
    private static final Bugzilla34xScraper SCRAPER_BUGZILLA34 = new Bugzilla34xScraper();

    /**
     * @returns Scraper object for the given repository type (jira3x, jira41, bugzilla34...).
     */
    public static IIssueScraper getScraperForRepoType( String typeName )
    {
       if( "jira3x".equals(typeName) )
          return SCRAPER_JIRA3X;

       if( "jira41".equals(typeName) )
          return SCRAPER_JIRA41;

       if( "jira50".equals(typeName) )
          return SCRAPER_JIRA50;

       if( "jira50-jboss".equals(typeName) )
          return SCRAPER_JIRA50_JBOSS;

       if( "bugzilla34".equals(typeName) )
          return SCRAPER_BUGZILLA34;

       log.error("  No suitable scraper for repository type: " + typeName);
       return null;
    }


}// class
