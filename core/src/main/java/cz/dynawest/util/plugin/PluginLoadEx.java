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
      this.moduleClass = moduleClass;
   }

   public PluginLoadEx(String moduleClass, String message, Throwable cause) {
      this.moduleClass = moduleClass;
   }

   public PluginLoadEx(String moduleClass, Throwable cause) {
      this.moduleClass = moduleClass;
   }
    
}// class

