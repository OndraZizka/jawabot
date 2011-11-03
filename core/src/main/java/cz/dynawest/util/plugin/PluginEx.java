package cz.dynawest.util.plugin;

/**
 * Generic exception
 */
public class PluginEx extends Exception {

   public PluginEx(Throwable cause) { super(cause); } 
   public PluginEx(String message, Throwable cause) { super(message, cause); } 
   public PluginEx(String message) { super(message);  }
   public PluginEx() {}    
   
}// class

