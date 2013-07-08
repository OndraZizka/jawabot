
package org.jboss.jawabot.config;

import java.io.File;
import java.io.FileInputStream;
import org.jboss.jawabot.ex.JawaBotIOException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.jawabot.config.beans.*;

/**
 *
 * @author Ondrej Zizka
 */
public class JaxbConfigPersister
{
   private static final Logger log = LoggerFactory.getLogger(JaxbConfigPersister.class);


   private String filePath;
   private Writer writer = null;

   
   /**
    *  @param filePath  The path to be used by load() and write().
    */
   public JaxbConfigPersister( String filePath ) {
      assert( filePath != null );
      this.filePath = filePath;
   }
   


   
   /**
    * Loads the configuration from the file (set by setFilePath).
    * @returns new JawaBot configured according to the loaded configuration.
    */
   public ConfigBean load() throws JawaBotIOException
   {
       log.info( "Looking for config: " + this.filePath );

       try {
           File readFrom = new File( this.filePath );
           
           // Try filesystem, then classpath.
           InputStream is;
           if( readFrom.exists() ) {
               log.info( "    Found in filesystem." );
               is = new FileInputStream( this.filePath );
           } else {
               log.info( "    Loading config from classpath." );
               is = JaxbConfigPersister.class.getClassLoader().getResourceAsStream( this.filePath );
               readFrom = null;
           }
           if( null == is ) {
               throw new JawaBotIOException( "Can't find '" + this.filePath + "' in filesystem nor classpath." );
           }

           // JAXB load
           InputStreamReader reader = new InputStreamReader( is );
           JAXBContext jc = JAXBContext.newInstance( ConfigBean.class );
           Unmarshaller mc = jc.createUnmarshaller();
           ConfigBean configBean = (ConfigBean) mc.unmarshal( reader );
           configBean.setReadFrom( readFrom );
           
           configBean.recreatePluginsMap(); // Plugin ID -> PluginBean
           return configBean;
       }
       catch( /*JAXB*/ Exception ex ) {
           throw new JawaBotIOException( ex );
       }
   }


   /**
    *
    * @param bot
    */
   public void merge( ConfigBean bot ) throws JawaBotIOException {
      throw new UnsupportedOperationException();
   }


   /**
    *
    * @param bot
    * TODO: Move to the JawaBot class.
    */
   public void save( ConfigBean configBean ) throws JawaBotIOException {

        // Store it to a XML.
        try {
            Writer writer = this.writer;
            if( null == writer )
                writer = new FileWriter( this.filePath );

            JAXBContext jc = JAXBContext.newInstance( ConfigBean.class );
            Marshaller mc = jc.createMarshaller();
            mc.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
            mc.marshal( configBean, writer );
        }
        catch(/*JAXB*/ Exception ex ) {
            throw new JawaBotIOException( ex );
        }
   }


    
    
   /** The given writer will be used when writing via JAXB. */
    public void setWriter(Writer writer) {
        this.writer = writer;
    }
    
}// class
