package kowi2003.core.common.misc.functions;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

/**
 * Just a normal supplier but with a nonnull javax annotation to get the null checks to work properly
 */
public interface NonnullSupplier<T> extends Supplier<T> {
    
    @Nonnull T get();

}
