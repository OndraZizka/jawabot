
package org.jboss.jawabot.plugin.jira.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.jboss.jawabot.plugin.jira.config.beans.ConfigBean;

/**
 *
 * @author Ondrej Zizka
 */
public class JaxbConfigReader
{
   private static final Logger log = Logger.getLogger( JaxbConfigReader.class.getName() );


   private String filePath;


   /** Blah blah. */
   public JaxbConfigReader( String filePath ) {
      this.filePath = filePath;
   }



   /**
    * Loads the configuration from the file (set by setFilePath).
    */
   public ConfigBean load() throws JiraBotIOException
   {
      log.info("Loading config from: "+this.filePath);

      try{
         // Try filesystem, then classpath.
         InputStream is;
         if( new File( this.filePath ).exists() ){
            log.info("Loading config from the filesystem.");
            is = new FileInputStream( this.filePath );
         }else{
            log.info("Loading config from the classpath.");
            is = JaxbConfigReader.class.getClassLoader().getResourceAsStream(this.filePath);
         }
         if( null == is )
            throw new JiraBotIOException("Can't find '"+this.filePath+"'");

         InputStreamReader reader = new InputStreamReader( is );


         JAXBContext jc = JAXBContext.newInstance( ConfigBean.class );
         Unmarshaller mc = jc.createUnmarshaller();
         ConfigBean config = (ConfigBean) mc.unmarshal(reader);

         return config;
      }
		catch (/*JAXB*/Exception ex) {
			throw new JiraBotIOException( ex );
		}
   }

}// class
