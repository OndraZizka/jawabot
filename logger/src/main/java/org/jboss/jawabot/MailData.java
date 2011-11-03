
package org.jboss.jawabot;

/**
 *
 * @author Ondrej Zizka
 */
public class MailData {

   public final String subject;
   public String messageBody;
   public String fromName;

   public MailData(String subject, String messageBody) {
      this.subject = subject;
      this.messageBody = messageBody;
   }

}// class
