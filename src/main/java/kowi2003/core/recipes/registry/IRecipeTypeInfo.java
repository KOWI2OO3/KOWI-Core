package kowi2003.core.recipes.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * An interface defining the structure of a recipe type container
 * 
 * @author KOWI2003
 */
public interface IRecipeTypeInfo {

    /**
     * Gets the name of the recipe type
     * @return the name of the recipe type
     */
    public String name();

    /**
     * Gets the id of the recipe type
     * @return the resourcelocation id of the recipe type
     */
	public ResourceLocation id();

    /**
     * Gets the serializer used for the recipe type, this defines how the json should be formatted
     * @param <T> the recipe serializer type
     * @return the serializer for this recipe type
     */
	public <T extends RecipeSerializer<?>> T serializer();

    /**
     * Gets the actual recipe type in the container 
     * @param <T> the recipe type
     * @return the recipe type of this container
     */
	public <T extends RecipeType<?>> T type();

    /**
     * Creates a simple recipe type to be used in registry
     * @param <T> the recipe class of the recipe type
     * @param id the id under which the type should be created
     * @return the simple recipe type
     */
    static <T extends Recipe<?>> RecipeType<T> simpleType(ResourceLocation id) {
		final String stringId = id.toString();
		return new RecipeType<T>() {
			public String toString() { return stringId; }
		};
	}

}
