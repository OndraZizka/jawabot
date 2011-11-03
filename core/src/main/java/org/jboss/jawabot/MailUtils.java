
package org.jboss.jawabot;


import org.jboss.jawabot.ex.JawaBotException;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.jboss.jawabot.config.beans.ConfigBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 *
 * @author Ondrej Zizka
 */
public class MailUtils {
   private static final Logger log = LoggerFactory.getLogger( MailUtils.class );

   private ConfigBean config;

   public MailUtils(ConfigBean config) {
      this.config = config;
   }

   
   
   /**
    * Sends a mail announcement about user's action.
    */
   public void sendMail( MailData mail ) throws JawaBotException {

        log.debug( String.format("Sending mail: host %s, to %s, from <%s>",
            config.settings.smtpHost,
            config.settings.announceEmailTo,
            config.settings.announceEmailFrom
        ) );

		try {
			SimpleEmail email = new SimpleEmail();
			email.setHostName( config.settings.smtpHost );
			email.addTo( config.settings.announceEmailTo ); // TODO: Let it depend on the resource's group.
			email.setFrom( config.settings.announceEmailFrom, mail.fromName );
			email.setSubject( mail.subject );
			email.setMsg(  mail.messageBody );
			email.send();
		} catch( EmailException ex ) {
			throw new JawaBotException("Can't mail to "+config.settings.announceEmailTo+": "+ex.getMessage(), ex);
		}
   }

}// class
