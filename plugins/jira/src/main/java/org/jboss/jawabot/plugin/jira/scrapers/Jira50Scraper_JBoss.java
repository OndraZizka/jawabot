
package org.jboss.jawabot.plugin.jira.scrapers;

import com.gargoylesoftware.htmlunit.html.HtmlPage;



/**
 * Responsible for getting an information from the given repo for the given issue ID.
 *
 * @author Ondrej Zizka
 */
public class Jira50Scraper_JBoss extends Jira50Scraper {
    
    
    /**
     *  JBoss has SSO, so Jira login is redirected to https://sso.jboss.org/login?...
     */
    @Override
    protected boolean isLoginPage( HtmlPage page ){
        return page.getWebResponse().getWebRequest().getUrl().toExternalForm().contains("sso.jboss.org/login");
    }

}