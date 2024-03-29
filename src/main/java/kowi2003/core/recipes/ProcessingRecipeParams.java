package kowi2003.core.recipes;

import kowi2003.core.recipes.ingredient.ProcessingFluidIngredient;
import kowi2003.core.recipes.ingredient.ProcessingIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

/**
 * An small internal container class which defines the base fields of the processing recipe
 * 
 * @author KOWI2003
 */
class ProcessingRecipeParams {
    
    protected ResourceLocation id;
    protected NonNullList<ProcessingIngredient> ingredients;
    protected NonNullList<ProcessingResult> results;
    protected NonNullList<ProcessingFluidIngredient> fluidIngredients;
    protected NonNullList<FluidStack> fluidResults;
    protected int processingDuration;

    public ProcessingRecipeParams(ResourceLocation id) {
        this.id = id;
        ingredients = NonNullList.create();
        results = NonNullList.create();
        fluidIngredients = NonNullList.create();
        fluidResults = NonNullList.create();
        processingDuration = 0;
    }

}
