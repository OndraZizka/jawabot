package org.jboss.jawabot.plugin.reserv.config;

import java.io.Serializable;
import javax.xml.bind.annotation.*;

/**
 *
 * @author Ondrej Zizka
 */
public class ReservPluginSettingsBean implements Serializable {

    // Mail - should it be in a plugin, or core?
    @XmlAttribute public String smtpHost = "smtp.corp.redhat.com";
    @XmlAttribute public String announceEmailTo = "jboss-qa-brno@redhat.com";
    @XmlAttribute public String announceEmailFrom = "jawabot-no-reply@redhat.com";
    @XmlAttribute public String announceDefaultChannel = "#jbosssoaqa";

}// class
