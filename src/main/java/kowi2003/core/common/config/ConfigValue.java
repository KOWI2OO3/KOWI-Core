package kowi2003.core.common.config;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * defines display attributes of the value in the config, defining a display name and wether and what the description of the field is
 * keep either null or empty to use the default values
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface ConfigValue {
    
    String displayName();
    String description();

}
