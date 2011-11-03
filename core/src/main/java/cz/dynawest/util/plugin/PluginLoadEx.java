package cz.dynawest.util.plugin;

/**
 * Exception with plugin class name
 *  @author Ondrej Zizka
 */
public class PluginLoadEx extends PluginEx {
   final String moduleClass;

   public String getModuleClass() {
      return moduleClass;
   }

   public PluginLoadEx(String moduleClass) {
      this.moduleClass = moduleClass;
   }

   public PluginLoadEx(String moduleClass, String message) {
      super(message);
      this.moduleClass = moduleClass;
   }

   public PluginLoadEx(String moduleClass, String message, Throwable cause) {
      super(message, cause);
      this.moduleClass = moduleClass;
   }

   public PluginLoadEx(String moduleClass, Throwable cause) {
      super(cause);
      this.moduleClass = moduleClass;
   }
    
}// class

