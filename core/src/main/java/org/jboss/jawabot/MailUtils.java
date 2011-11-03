
package org.jboss.jawabot;


import org.jboss.jawabot.ex.JawaBotException;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;
import org.jboss.jawabot.config.beans.ConfigBean;



/**
 *
 * @author Ondrej Zizka
 */
public class MailUtils {
   private static final Logger log = Logger.getLogger( MailUtils.class );

   private JawaBot jawaBot;

   public MailUtils(JawaBot jawaBot) {
      this.jawaBot = jawaBot;
   }



   /**
    * Sends a mail announcement about user's action.
    */
   public void sendMail( MailData mail ) throws JawaBotException {

      ConfigBean cnf = this.jawaBot.getConfig();

      log.debug( String.format("Sending mail: host %s, to %s, from <%s>",
			cnf.settings.smtpHost,
			cnf.settings.announceEmailTo,
			cnf.settings.announceEmailFrom
      ) );

		try {
			SimpleEmail email = new SimpleEmail();
			email.setHostName( cnf.settings.smtpHost );
			email.addTo( cnf.settings.announceEmailTo ); // TODO: Let it depend on the resource's group.
			email.setFrom( cnf.settings.announceEmailFrom, mail.fromName );
			email.setSubject( mail.subject );
			email.setMsg(  mail.messageBody );
			email.send();
		} catch( EmailException ex ) {
			throw new JawaBotException("Can't mail to "+cnf.settings.announceEmailTo+": "+ex.getMessage(), ex);
		}
   }

}// class
