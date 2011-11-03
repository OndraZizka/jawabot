
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

/**
 *
 * @author Ondrej Zizka
 */
public class JaxbGenericPersister<T>
{
   private static final Logger log = LoggerFactory.getLogger(JaxbGenericPersister.class);


   private String filePath;
   private Writer writer = null;
   private Class<T> cls;

   
   /**
    *  @param filePath  The path to be used by load() and write().
    */
   public JaxbGenericPersister( String filePath, Class<T> cls ) {
      assert( filePath != null );
      this.filePath = filePath;
      this.cls = cls;
   }
   


   
   /**
    * Loads the configuration from the file (set by setFilePath).
    * @returns new JawaBot configured according to the loaded configuration.
    */
   public T load() throws JawaBotIOException
   {
       log.info( "Looking for config: " + this.filePath );

       try {
           // Try filesystem, then classpath.
           InputStream is;
           if( new File( this.filePath ).exists() ) {
               log.info( "    Found in filesystem." );
               is = new FileInputStream( this.filePath );
           } else {
               log.info( "    Loading config from classpath." );
               is = JaxbGenericPersister.class.getClassLoader().getResourceAsStream( this.filePath );
           }
           if( null == is ) {
               throw new JawaBotIOException( "Can't find '" + this.filePath + "'" );
           }

           InputStreamReader reader = new InputStreamReader( is );
           JAXBContext jc = JAXBContext.newInstance( this.cls );
           Unmarshaller um = jc.createUnmarshaller();
           T configBean = (T) um.unmarshal( reader );
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
   public void merge( T cb ) throws JawaBotIOException {
      throw new UnsupportedOperationException();
   }


   /**
    *
    * @param bot
    * TODO: Move to the JawaBot class.
    */
	public void save( T configBean ) throws JawaBotIOException {


        // Store it to a XML.
        try {
            Writer writer = this.writer;
            if( null == writer )
                writer = new FileWriter( this.filePath );
            
            if( ! this.cls.isAssignableFrom( configBean.getClass() ) )
                throw new JawaBotIOException("Can't save " + configBean.getClass().getName() + " - expected " + this.cls.getName() );

            JAXBContext jc = JAXBContext.newInstance( configBean.getClass() );
            Marshaller mar = jc.createMarshaller();
            mar.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
            mar.marshal( configBean, writer );
        }
        catch( /*JAXB*/Exception ex ) {
            throw new JawaBotIOException( ex );
        }

	}


    
    
   /** The given writer will be used when writing via JAXB. */
    public void setWriter( Writer writer ) {
        this.writer = writer;
    }
    
}// class
