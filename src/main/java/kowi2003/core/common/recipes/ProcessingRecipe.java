package kowi2003.core.common.recipes;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import kowi2003.core.common.recipes.ingredient.ProcessingFluidIngredient;
import kowi2003.core.common.recipes.ingredient.ProcessingIngredient;
import kowi2003.core.common.recipes.registry.IRecipeTypeInfo;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * An generic and user friendly recipe which can handle recipes with multiple stacked inputs and outputs, including
 * optional outputs or outputs of certain ranges, also can it handle fluid ingredients and results
 * 
 * Thereby making it an suitable recipe type for any and all modded recipes that need to be added to custom machines/crafting interfaces
 * 
 * @author KOWI2003
 */
public abstract class ProcessingRecipe<T extends Container> implements Recipe<T> {

    private ResourceLocation id;
	private NonNullList<ProcessingIngredient> ingredients;
	private NonNullList<ProcessingResult> result;
	private NonNullList<ProcessingFluidIngredient> fluidIngredients;
	private NonNullList<FluidStack> fluidResult;
	private int duration;
	
	private IRecipeTypeInfo typeInfo;
	private RecipeType<?> type;
	private RecipeSerializer<?> serializer;

    /**
     * Creats an processing recipe based on the recipe type info and the recipe parameters supplied
     * @param info the type info for the recipe
     * @param params the recipe parameters
     */
    public ProcessingRecipe(IRecipeTypeInfo info, ProcessingRecipeParams params) {
        typeInfo = info;
        type = info.type();
        serializer = info.serializer();
        
        id = params.id;
        ingredients = params.ingredients;
        fluidIngredients = params.fluidIngredients;
        result = params.results;
        fluidResult = params.fluidResults;

        duration = params.processingDuration;
    }

    /**
     * checks to see if the items correspond to the recipe ingredients
     */
    @Override
    public abstract boolean matches(@Nonnull T container, @Nonnull Level level);
    
    /**
     * checks to see if the fluids correspond to the recipe ingredients
     */
    public boolean matches(@Nonnull IFluidHandler[] tanks, @Nonnull Level world) { return true; }

    /**
     * checks to see if the recipe is valid for both items and fluids
     * @param container the item container check
     * @param tanks the fluid container to check 
     * @param level the world reference
     * @return whether the recipe matches the containers
     */
	public boolean matchesTotal(@Nonnull T container, @Nonnull IFluidHandler[] tanks, @Nonnull Level level) {
		return matches(container, level) && matches(tanks, level);
	}

    /**
     * gets the resulting item from the recipe
     */
    @Override
    public ItemStack assemble(@Nonnull T container, @Nonnull RegistryAccess registryAccess) {
		return getResultItem();
    }

    /**
     * Gets the primairy resulting item from the recipe
     * @return the primairy resulting item
     */
    public ItemStack getResultItem(@Nonnull RegistryAccess registryAccess) {
		return getResultItem();
	}

    /**
     * Gets the primairy resulting item from the recipe
     * @return the primairy resulting item
     */
    public ItemStack getResultItem() {
		return getResult().isEmpty() ? ItemStack.EMPTY : getResult().get(0).getItem();
	}

    /**
     * Gets the primairy resulting fluid from the recipe
     * @return the primairy resulting fluid
     */
	public FluidStack getResultFluid() {
		return getFluidResults().isEmpty() ? FluidStack.EMPTY : getFluidResults().get(0);
	}

    @Override
	public boolean canCraftInDimensions(int width, int height) {
        // Forcing the recipe to work, because we never use this check
		return true;
	}

    @Override
    public boolean isSpecial() {
        // the recipe is a special boy ;)
        return true;
    }

    @Override
    public String getGroup() {
        // Setting a default for the group as the custom machines don't (usually) use the recipe book
        return "processing";
    }

    /**
     * Gets the id of the recipe
     */
    @Override
    public ResourceLocation getId() {
        return id;
    }

    /**
     * Gets the serializer for the recipe
     */
    @Override
    public RecipeSerializer<?> getSerializer() {
		return serializer;
    }

    /**
     * Gets the recipe type
     */
    @Override
	public RecipeType<?> getType() {
		return type;
	}
	
    /**
     * Gets the recipe type info as a whole
     * @return the recipe type info for this recipe
     */
	public IRecipeTypeInfo getTypeInfo() {
		return typeInfo;
	}

    @Override
    public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		getProcessingIngredients().forEach(ingr -> ingredients.add(ingr.getIngredient()));
		return ingredients;
    }
	
    /**
     * Gets the processing ingredients list
     * @return a nonnull list containing the processing ingredients
     */
	public NonNullList<ProcessingIngredient> getProcessingIngredients() {
		return ingredients;
	}
	
    /**
     * Gets the processing fluid ingredients list
     * @return a nonnull list containing the processing fluid ingredients
     */
	public NonNullList<ProcessingFluidIngredient> getFluidIngredients() {
		return fluidIngredients;
	}
	
    /**
     * Gets the list of processing results
     * @return a nonnull list containing the results
     */
	public NonNullList<ProcessingResult> getResult() {
		return result;
	}
	
    /**
     * Gets the list of fluid results
     * @return a nonnull list containing the fluid results
     */
	public NonNullList<FluidStack> getFluidResults() {
		return fluidResult;
	}
	
	public int getDuration() {
		return duration;
	}

    /**
     * reads additional data from the json object, this data could be unique for a specific recipe
     * @param json the json object to get the additional data from
     */
	public void readAdditional(JsonObject json) {}

     /**
     * reads additional data from the network buffer, this data could be unique for a specific recipe
     * @param buffer the network buffer to get the additional data from
     */
	public void readAdditional(FriendlyByteBuf buffer) {}

    /**
     * writes additional data, which can be unique or specific for a certain recipe, to the json object
     * @param json te json object to write the additional data to
     */
	public void writeAdditional(JsonObject json) {}

    /**
     * writes additional data, which can be unique or specific for a certain recipe, to the network buffer
     * @param json te network buffer to write the additional data to
     */
	public void writeAdditional(FriendlyByteBuf buffer) {}
}
