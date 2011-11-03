
package org.jboss.jawabot.plugin.jira.scrapers;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;


/**
 *
 * @author Ondrej Zizka
 */
public abstract class HtmlUnitAbstractScraper implements IssueScraper {

   protected final WebClient wc;


   public HtmlUnitAbstractScraper() {
        // Create HTMLUnit WebClient.
        wc = new WebClient(BrowserVersion.FIREFOX_3);
        wc.setCssEnabled(false);
        wc.setJavaScriptEnabled(false);
   }


}// class
