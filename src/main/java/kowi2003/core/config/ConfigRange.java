package kowi2003.core.config;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

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
