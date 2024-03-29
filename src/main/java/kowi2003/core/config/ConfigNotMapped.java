package kowi2003.core.config;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defining that a certain field will not be mapped to a config spec
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface ConfigNotMapped {
    
}
