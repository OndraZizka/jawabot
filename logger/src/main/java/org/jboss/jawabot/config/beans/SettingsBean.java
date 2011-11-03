
package org.jboss.jawabot.config.beans;

import java.io.Serializable;
import javax.xml.bind.annotation.*;

/**
 *
 * @author Ondrej Zizka
 */
public class SettingsBean implements Serializable {

   @XmlAttribute public boolean verbose = true;
   @XmlAttribute public boolean acceptInvitation = true;
   @XmlAttribute public int messageDelay = 666;
   @XmlAttribute public boolean leaveOnAsk = true;
   @XmlAttribute public String adminUser = "ozizka";
   @XmlAttribute public String smtpHost = "smtp.corp.redhat.com";
   @XmlAttribute public String announceEmailTo = "jboss-qa-brno@redhat.com";
   @XmlAttribute public String announceEmailFrom = "jawabot-no-reply@redhat.com";
   @XmlAttribute public String announceDefaultChannel = "#jbosssoaqa";
   @XmlAttribute public String debugChannel = "#some";
   @XmlAttribute public boolean unsecuredShutdown = false;

   
   // Catch-all HashMap. Is it needed?
   //@XmlAnyAttribute
   //public Map<String, String> other = new HashMap();


}// class
