package cz.dynawest.util.plugin.cdi;

/**
 *  
 *  +- interface IPluginLifeCycle
 *    +- @SingleOfThisType interface MyPlugin extends IPluginLifeCycle
 *       +- class DummyMyPluginImpl
 *       +- class RealMyPluginImpl
 * 
 * -> Only one of the two plug-ins should be loaded.
 *    Reason to load that one should be logged.
 *    Other options should be logged.
 * 
 *  @author Ondrej Zizka
 */
public @interface SingleOfThisType {
   
}// class

