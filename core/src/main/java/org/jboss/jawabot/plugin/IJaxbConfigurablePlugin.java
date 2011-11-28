package org.jboss.jawabot.plugin;

/**
 *
 * @author Ondrej Zizka
 */
public interface IJaxbConfigurablePlugin<T> {
    
    public Class<T> getJaxbBeanClass();
    
    public void applyConfig(T configBean);
    
}// class

