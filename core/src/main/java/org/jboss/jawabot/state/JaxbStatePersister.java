
package org.jboss.jawabot.state;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger; import org.slf4j.LoggerFactory; 
import org.jboss.jawabot.ex.JawaBotIOException;
import org.jboss.jawabot.state.beans.StateBean;

/**
 *
 * @author Ondrej Zizka
 */
public class JaxbStatePersister implements StatePersister
{
   private static final Logger log = LoggerFactory.getLogger(JaxbStatePersister.class);

   public static final String DEFAULT_STATE_FILE_PATH = "JawaBotState.xml";

   private String filePath = DEFAULT_STATE_FILE_PATH;
   public JaxbStatePersister setFilePath(String filePath) {
      this.filePath = StringUtils.defaultIfBlank(filePath, DEFAULT_STATE_FILE_PATH);
      return this;
   }
   

   private Writer writer = null;
   public JaxbStatePersister setWriter(Writer writer) {  this.writer = writer;  return this; }


   // Const

   /** Sets "JawaBotState.xml" as default persistence file. */
   public JaxbStatePersister() { }

   /** Sets given persistence file. */
   public JaxbStatePersister( String filePath ) { this.filePath = filePath; }

   /** Sets given writer to be used when writing the XML output. */
   public JaxbStatePersister(Writer writer) {
      this.writer = writer;
   }


   
   /**
    * 
    * @return
    */
   @Override
   public StateBean load() throws JawaBotIOException
   {
      log.info("  Reading state from: "+this.filePath);
      try{
         FileReader reader = new FileReader(this.filePath);

         JAXBContext jc = JAXBContext.newInstance( StateBean.class );
         Unmarshaller mc = jc.createUnmarshaller();
         StateBean state = (StateBean) mc.unmarshal(reader);
         return state;
      }
      catch ( FileNotFoundException ex) {
         //throw new JawaBotIOException( "State file not found: "+filePath, ex );
         log.warn("  State file not found - starting clean. "+this.filePath);
         return new StateBean(); // Empty.
      }
		catch ( JAXBException ex) {
			throw new JawaBotIOException( "  Error reading state file '"+this.filePath+"': "+ex.getMessage(), ex );
		}
   }


   /**
    *
    * @param bot
    */
   public void merge( StateBean state ) throws JawaBotIOException {
      throw new UnsupportedOperationException();
   }


   /**
    *
    * @param bot
    */
	public void save( StateBean state ) throws JawaBotIOException {

      // Store it to a XML.
		try {
         Writer writer = this.writer;
         if( null == writer )
            writer = new FileWriter(filePath);

         JAXBContext jc = JAXBContext.newInstance( StateBean.class );
         Marshaller mc = jc.createMarshaller();
         mc.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			mc.marshal( state, writer );
		}
		catch (/*JAXB*/Exception ex) {
			throw new JawaBotIOException( ex );
		}
      
	}



   @Override
   public String toString() {
      return String.format( "JaxbStatePersister{ filePath: %s }", this.filePath );
   }





}// class
