package cz.dynawest.util.plugin;


/**
 *  
 *  @author Ondrej Zizka
 */
public interface IPluginLifeCycle <TInit, TEx extends Exception> {
   
   public void initModule ( TInit initObject ) throws TEx;
   
   public void startModule() throws TEx;
   
   public void stopModule() throws TEx;
   
   public void destroyModule() throws TEx;
   
}// class

