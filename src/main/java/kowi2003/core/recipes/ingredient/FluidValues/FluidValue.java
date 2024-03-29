package kowi2003.core.recipes.ingredient.FluidValues;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import net.minecraftforge.fluids.FluidStack;

/**
 * This interface supplies an generic interface to check fluids
 * which then in turn are used in fluid ingredients
 * 
 * @author KOWI2003
 */
public interface FluidValue {
    
    /**
     * gets the list of fluids valid for a certain ingredient this is based on the
     * value supplied in the implementation of the interface
     * @return the collection of valid fluids
     */
    Collection<FluidStack> getFluids();

    /**
     * Serializes the value into a json object to be used to embed in other json data structures
     * @return the serialized fluid value in json format
     */
    @Nonnull
    JsonObject serialize();
}
