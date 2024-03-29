package kowi2003.core.recipes;

import javax.annotation.Nonnull;

/**
 * A simple factory interface to easily create an factory object which will create a recipe based on the parameters given
 * 
 * @author KOWI2003
 */
public interface ProcessingRecipeFactory<T extends ProcessingRecipe<?>> {
    
    /**
     * Creates a new recipe instance based on the supplied recipe parameters
     * @param params the recipe parameters used to create the recipe
     * @return a new recipe instance 
     */
    @Nonnull T create(ProcessingRecipeParams params);
}
