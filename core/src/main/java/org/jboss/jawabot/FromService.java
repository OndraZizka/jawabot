/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.jawabot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Ondrej Zizka
 */
@Retention( RetentionPolicy.RUNTIME )
//@Target({ ElementType.METHOD, ElementType.FIELD })
@javax.inject.Qualifier
public @interface FromService {
   
}// interface

