package kowi2003.core.common.config;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines the range of a config value, Note this only works on numbers aka int, float, double
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface ConfigRange {
    
	double min();
	double max();

}
