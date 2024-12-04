package kowi2003.core.common.config;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defining that a certain field will not be mapped to a config spec
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface ConfigNotMapped {
    
}
