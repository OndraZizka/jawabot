
package org.jboss.jawabot.plugin.jira.config;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.jboss.jawabot.plugin.jira.config.beans.ConfigBean;
import org.jboss.jawabot.plugin.jira.config.core.JiraBot;

/**
 *
 * @author Ondrej Zizka
 */
public class JaxbConfigWriter {

	public static void write( JiraBot bot, File toFile ) throws JiraBotIOException {
		try {

			JAXBContext jc = JAXBContext.newInstance( ConfigBean.class );
			Marshaller mc = jc.createMarshaller();
			mc.marshal(new Object(), System.out);

		}
		catch (JAXBException ex) {
			throw new JiraBotIOException( ex );
		}
	}

}// class
