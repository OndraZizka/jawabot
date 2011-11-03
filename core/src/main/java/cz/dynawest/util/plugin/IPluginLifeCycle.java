package cz.dynawest.util.plugin;

/**
 *  
 *  @author Ondrej Zizka
 */
public interface IPluginLifeCycle<TInitObject extends Object> {
   
   public void initModule( TInitObject initObject ) throws PluginEx;
   
   public void startModule() throws PluginEx;
   
   public void stopModule() throws PluginEx;
   
   public void destroyModule() throws PluginEx;
   
}// class

