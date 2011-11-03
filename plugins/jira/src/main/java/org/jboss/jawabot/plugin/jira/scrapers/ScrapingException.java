
package org.jboss.jawabot.plugin.jira.scrapers;

import org.jboss.jawabot.plugin.jira.core.JiraBotException;

/**
 *
 * @author Ondrej Zizka
 */
public class ScrapingException extends JiraBotException {

   public ScrapingException(String message) {
      super(message);
   }

   public ScrapingException(String message, Throwable cause) {
      super(message, cause);
   }

   public ScrapingException(Throwable cause) {
      super(cause);
   }

   

}// class
