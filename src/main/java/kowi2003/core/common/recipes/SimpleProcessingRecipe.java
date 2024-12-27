package kowi2003.core.common.recipes;

import javax.annotation.Nonnull;

import kowi2003.core.common.helpers.RecipeHelper;
import kowi2003.core.common.recipes.registry.IRecipeTypeInfo;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * An simple implementation of the processing recipe
 * 
 * @author KOWI2003
 */
public class SimpleProcessingRecipe<T extends Container>  extends ProcessingRecipe<T> {

    public SimpleProcessingRecipe(IRecipeTypeInfo info, ProcessingRecipeParams params) {
        super(info, params);
    }

    @Override
    public boolean matches(@Nonnull T container, @Nonnull Level level) {
        return RecipeHelper.check(container, 0, getProcessingIngredients().size(), getProcessingIngredients());
    }

    @Override
    public boolean matches(@Nonnull IFluidHandler[] tanks, @Nonnull Level world) {
        return RecipeHelper.check(tanks, getFluidIngredients());
    }
    
}
