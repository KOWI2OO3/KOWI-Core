package kowi2003.core.recipes.registry;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;

/**
 * A implementation of the IRecipeTypeInfo to function as the return type for the registered recipetype info. 
 * As gotten from registring a recipe type using the {@link RecipeRegistry RecipeRegistry}
 * 
 * @author KOWI2003
 */
record RecipeTypeInfo<T extends Recipe<?>>(String name, ResourceLocation id, RegistryObject<RecipeSerializer<T>> serializerObject, RegistryObject<RecipeType<T>> typeObject, 
        Supplier<RecipeType<T>> typeSupplier) implements IRecipeTypeInfo {

    @Override
    @SuppressWarnings("unchecked")
    public <K extends RecipeSerializer<?>> K serializer() {
        return (K) serializerObject().get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K extends RecipeType<?>> K type() {
        return (K) typeSupplier().get();
    }
    
}
