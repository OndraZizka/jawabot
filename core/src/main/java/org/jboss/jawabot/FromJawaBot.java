package org.jboss.jawabot;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * CDI qualifier.
 * 
 * @author Ondrej Zizka
 */
@Retention( RetentionPolicy.RUNTIME )
//@Target({ ElementType.METHOD, ElementType.FIELD })
@javax.inject.Qualifier
public @interface FromJawaBot {
   
}// interface

