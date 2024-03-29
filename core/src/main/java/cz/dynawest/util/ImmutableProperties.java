
package cz.dynawest.util;

import java.io.*;
import java.util.*;

/**
 * Immutable version of Properties object.
 * Instances work transparently as Properties object 
 * unless you try to change it.
 * 
 * Calling any method that can potentially change the properties
 * throws UnsupportedOperationException("Immutable Properties object - can't be changed.");
 * 
 * @author Ondřej Žižka
 * @see java.util.Properties
 * @version 1.0
 */
public class ImmutableProperties extends java.util.Properties {

  /**
   * Constructs an immutable copy of the given Properties object.
   * @param original  Properties object to copy.
   */
  public ImmutableProperties( Properties original ) {
    //super.defaults = original;
    super.putAll( original );
  }
  
  
  
  // -- Properties overrides -- 
  
  @Override
  public Object setProperty( String key, String val ){
    throw new UnsupportedOperationException("Immutable Properties object - can't be changed.");
  }

  //@Override
  public synchronized void load( Reader arg0 ) throws IOException {
    throw new UnsupportedOperationException("Immutable Properties object - can't be changed.");
  }


  @Override
  public synchronized void load( InputStream arg0 ) throws IOException {
    throw new UnsupportedOperationException("Immutable Properties object - can't be changed.");
  }


  @Override
  public synchronized void loadFromXML( InputStream arg0 ) throws IOException, InvalidPropertiesFormatException {
    throw new UnsupportedOperationException("Immutable Properties object - can't be changed.");
  }
  
  
  
  // -- Hashtable overrides --

  @Override
  public synchronized void clear() {
    throw new UnsupportedOperationException("Immutable Properties object - can't be changed.");
  }


  @Override
  public synchronized Object put( Object arg0, Object arg1 ) {
    throw new UnsupportedOperationException("Immutable Properties object - can't be changed.");
  }


  @Override
  public synchronized void putAll( Map<? extends Object, ? extends Object> arg0 ) {
    throw new UnsupportedOperationException("Immutable Properties object - can't be changed.");
  }


  @Override
  public synchronized Object remove( Object arg0 ) {
    throw new UnsupportedOperationException("Immutable Properties object - can't be changed.");
  }

}// class ImmutableProperties
