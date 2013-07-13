package org.jboss.jawabot.config.beans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
@XmlRootElement
public class PersistenceBean {

    @XmlElement
    public JdbcBean jdbc;
    
    @XmlAttribute public String scanForEntities="false";
    @XmlAttribute public String cacheProvider="org.hibernate.cache.NoCacheProvider";
    @XmlAttribute public String dialect="org.hibernate.dialect.MySQL5Dialect";
    

    //@XmlElement
    //Map<String, String> properties;

    @XmlRootElement
    public static class JdbcBean {
        
        @XmlAttribute public String user;
        @XmlAttribute public String pass;
        @XmlAttribute public String url;
        @XmlAttribute public String driver;
        
    }// class
   
}// class

