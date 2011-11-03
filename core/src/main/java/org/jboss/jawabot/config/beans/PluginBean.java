package org.jboss.jawabot.config.beans;

import javax.xml.bind.annotation.XmlAttribute;

/**
 *  Plugin element.
 *  @author Ondrej Zizka
 */
public class PluginBean {
   
   @XmlAttribute
   public String id;
   
   @XmlAttribute
   public String config;
   
}// class

