package cz.dynawest.util.plugin;


/**
 *  
 *  @author Ondrej Zizka
 */
public interface IPluginLifeCycle <T> {
   
   public void initModule ( T initObject ) throws PluginEx;
   
   public void startModule() throws PluginEx;
   
   public void stopModule() throws PluginEx;
   
   public void destroyModule() throws PluginEx;
   
}// class

