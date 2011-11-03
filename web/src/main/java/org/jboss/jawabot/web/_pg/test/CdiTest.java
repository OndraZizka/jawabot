package org.jboss.jawabot.web._pg.test;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  
 *  @author Ondrej Zizka
 */
public class CdiTest {

    @Produces Logger getLogger(InjectionPoint injectionPoint) {
        return LoggerFactory.getLogger( injectionPoint.getMember().getDeclaringClass().getSimpleName() );
    }
    
}// class

