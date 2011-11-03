package cz.dynawest.util.plugin;


/**
 *  
 *  @author Ondrej Zizka
 */
public interface IPluginLifeCycle <TInit> {
   
   public void initModule ( TInit initObject ) throws PluginEx;
   
   public void startModule() throws PluginEx;
   
   public void stopModule() throws PluginEx;
   
   public void destroyModule() throws PluginEx;
   
}// class

