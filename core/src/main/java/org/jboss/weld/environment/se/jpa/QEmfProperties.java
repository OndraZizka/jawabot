package org.jboss.weld.environment.se.jpa;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

/**
 * Marks the source and target of EntityManagerFactory properties.
 * 
 * @author Ondrej Zizka, ozizka at redhat.com
 */
@Qualifier
@Retention( RUNTIME )
@Target( { METHOD, FIELD, PARAMETER, TYPE } )
public @interface QEmfProperties {
}