package org.jboss.jawabot.ex;

/**
 *
 * @author ondra
 */
public class ModuleNotFoundEx extends JawaBotException {
   
   private final String moduleClass;
   public String getModuleClass() { return moduleClass; }
   
   

   public ModuleNotFoundEx( String moduleClass ) {
      this.moduleClass = moduleClass;
   }
  

   public ModuleNotFoundEx( String moduleClass, String message) {
      super(message);
      this.moduleClass = moduleClass;
   }

   public ModuleNotFoundEx( String moduleClass, String message, Throwable cause ) {
      super(message, cause);
      this.moduleClass = moduleClass;
   }

   public ModuleNotFoundEx( String moduleClass, Throwable cause ) {
      super(cause);
      this.moduleClass = moduleClass;
   }
   
}
