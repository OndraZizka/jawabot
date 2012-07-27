package org.jboss.jawabot.config.beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *  
 *  @author Ondrej Zizka
 */
public class PluginsMapAdaptor extends XmlAdapter<PluginsListBean, Map<String,String>> {

    
    /**
     *  Converts given Map to List<PluginBean>.
     */
    @Override
    public PluginsListBean marshal( Map<String,String> map ) throws Exception
    {
        PluginsListBean pluginsMapBean = new PluginsListBean();
        
        List<PluginBean> pluginBeansList = pluginsMapBean.getPluginBeans();
        
        for ( Map.Entry<String,String> e : map.entrySet() ) {
            pluginBeansList.add( new PluginBean( e.getKey(), e.getValue()) );
        }
        return pluginsMapBean;
    }

    
    /**
     *  Converts given list of PluginMapBean's to a Map.
     */
    @Override
    public Map<String,String> unmarshal( PluginsListBean pluginsBean ) throws Exception 
    {
        Map<String,String> map = new HashMap<String,String>();
        
        for ( PluginBean pluginBean : pluginsBean.getPluginBeans() ) {
            map.put( pluginBean.id, pluginBean.configPath );
        }
        return map;
    }
}

// class

