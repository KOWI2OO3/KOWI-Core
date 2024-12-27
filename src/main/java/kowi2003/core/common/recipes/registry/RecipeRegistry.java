package kowi2003.core.common.recipes.registry;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import kowi2003.core.Core;
import kowi2003.core.common.recipes.ProcessingRecipe;
import kowi2003.core.common.recipes.ProcessingRecipeFactory;
import kowi2003.core.common.recipes.ProcessingRecipeSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * A simpler Recipe Register Wrapper which makes it possible to register a recipe type with a single register method call
 * 
 * @author KOWI2003
 */
public final class RecipeRegistry {
    
    /**
     * The RecipeRegistry for the KOWI Core
     */
    public static final RecipeRegistry CORE_REGISTRY = new RecipeRegistry(Core.MODID);


    // Internal minecraft recipe registers 
    private final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER;
    private final DeferredRegister<RecipeType<?>> TYPE_REGISTER;

    /**
     * Constructs a new Recipe Registry using the modid 
     * @param modid the id of the mod for which the registry is
     */
    public RecipeRegistry(@Nonnull String modid) {
        SERIALIZER_REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, modid);
        TYPE_REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, modid);
    }

    /**
     * Registers a recipe type using the given suppliers to the internal forge recipe registers. 
     * @param <T> the type of the recipe of the recipe type
     * @param id the id of the recipe type
     * @param serializerSupplier the supplier to give the serializer to the register
     * @param typeSupplier the supplier for the actual Recipe Type
     * @param registerType whether to register the actual Recipe Type, usefull for handling already existing recipe types
     * @return a recipe type info object which contains the necessary info about the recipe type
     */
    public <T extends Recipe<?>> IRecipeTypeInfo register(@Nonnull ResourceLocation id, @Nonnull Supplier<RecipeSerializer<T>> serializerSupplier, @Nonnull Supplier<RecipeType<T>> typeSupplier, boolean registerType) {
        var serializerObject = SERIALIZER_REGISTER.register(id.getPath(), serializerSupplier);
        RegistryObject<RecipeType<T>> typeObject;
        Supplier<RecipeType<T>> type;

        if(registerType) {
			typeObject = TYPE_REGISTER.register(id.getPath(), typeSupplier);
			type = typeObject;
		}else {
			typeObject = null;
			type = typeSupplier;
		}

        return new RecipeTypeInfo<>(id.getPath(), id, serializerObject, typeObject, type);
    }

    /**
     * Registers a recipe type using the given suppliers to the internal forge recipe registers. 
     * @param <T> the type of the recipe of the recipe type
     * @param id the id of the recipe type
     * @param serializerSupplier the supplier to give the serializer to the register
     * @return a recipe type info object which contains the necessary info about the recipe type
     */
    public <T extends Recipe<?>> IRecipeTypeInfo register(@Nonnull ResourceLocation id, @Nonnull Supplier<RecipeSerializer<T>> serializerSupplier) {
        final var name = id.getPath();

        var serializerObject = SERIALIZER_REGISTER.register(name, serializerSupplier);
        var typeObject = TYPE_REGISTER.register(name, () -> IRecipeTypeInfo.<T>simpleType(id));
        var type = typeObject;

        return new RecipeTypeInfo<>(name, id, serializerObject, typeObject, type);
    }

    /**
     * Registers a processing recipe type using the given factory to the internal forge recipe registers. 
     * @param <T> the type of the recipe of the recipe type
     * @param id the id of the recipe type
     * @param recipeFactory the processing recipe factory to build the recipe type from
     * @return a recipe type info object which contains the necessary info about the recipe type
     */
    public <T extends ProcessingRecipe<?>> IRecipeTypeInfo register(@Nonnull ResourceLocation id, @Nonnull ProcessingRecipeFactory<T> recipeFactory) {
        return register(id, () -> new ProcessingRecipeSerializer<>(recipeFactory));
    }

}