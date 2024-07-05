package kowi2003.core.recipes;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import kowi2003.core.recipes.ingredient.FluidIngredient;
import kowi2003.core.recipes.ingredient.ProcessingFluidIngredient;
import kowi2003.core.recipes.ingredient.ProcessingIngredient;
import kowi2003.core.utils.FluidHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;

/**
 * The default recipe serializer for processing recipes.
 * this class allows for json read and write and network read and writes
 * 
 * @author KOWI2003
 */
public class ProcessingRecipeSerializer<T extends ProcessingRecipe<?>> implements RecipeSerializer<T> {
    
    @Nonnull
    private final ProcessingRecipeFactory<T> factory;

    /**
     * Creates a new recipe serializer with the supplied factory to create the recipe instance
     * @param factory the factory defining how to create the recipe instance
     */
    public ProcessingRecipeSerializer(@Nonnull ProcessingRecipeFactory<T> factory) {
        this.factory = factory;
    }

    /**
     * Writes the recipe to the supplied json object
     * @param json the json object to write the recipe to
     * @param recipe the recipe to write to json
     */
    protected void writeToJson(@Nonnull JsonObject json, @Nonnull T recipe) {
        var jsonIngredients = new JsonArray();
        var jsonFluidIngredients = new JsonArray();
        var jsonOutputs = new JsonArray();
        var jsonFluidOutputs = new JsonArray();

        recipe.getIngredients().forEach(i -> jsonIngredients.add(i.toJson()));
        recipe.getFluidIngredients().forEach(i -> jsonFluidIngredients.add(i.toJson()));
        
        recipe.getResult().forEach(result -> jsonOutputs.add(ProcessingResult.toJson(result)));
        recipe.getFluidResults().forEach(result -> jsonFluidOutputs.add(FluidHelper.serializeFluidStack(result)));

        json.add("ingredients", jsonIngredients);
        json.add("fluid_ingredients", jsonFluidIngredients);
        json.add("results", jsonOutputs);
        json.add("fluid_results", jsonFluidOutputs);

        int duration = recipe.getDuration();
        if(duration > 0)
            json.addProperty("processingTime", duration);
        
        recipe.writeAdditional(json);
    }

    /**
     * Reads the recipe from the supplied json object and returns it under the recipeId supplied
     * @param recipeId the id of the recipe to be read
     * @param json the json to read the recipe from
     * @return the recipe that has been read from the json
     */
    protected T readFromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
        var builder = new ProcessingRecipeBuilder<>(factory, recipeId);
        NonNullList<ProcessingIngredient> ingredients = NonNullList.create();
        NonNullList<ProcessingFluidIngredient> fluidIngredients = NonNullList.create();
        NonNullList<ProcessingResult> results = NonNullList.create();
		NonNullList<FluidStack> fluidResults = NonNullList.create();

        // Parsing item ingredients
        var jsonIngredients = GsonHelper.getAsJsonArray(json, "ingredients", null);
        if(jsonIngredients != null)
        for(var element : jsonIngredients) {
            if(element.isJsonObject())
                ingredients.add(ProcessingIngredient.fromJson(json));
            else {
                var ingr = new ProcessingIngredient(Ingredient.fromJson(element));
				if(ingr != null && !ingr.getIngredient().isEmpty())
					ingredients.add(ingr);
            }
        }

        // Parsing fluid ingredients
        var jsonFluidIngredients = GsonHelper.getAsJsonArray(json, "fluid_ingredients", null);
        if(jsonFluidIngredients != null)
        for(var element : jsonFluidIngredients) {
            if(element.isJsonObject())
                fluidIngredients.add(ProcessingFluidIngredient.fromJson(json));
            else {
                var ingr = new ProcessingFluidIngredient(FluidIngredient.fromJson(element));
				if(ingr != null && !ingr.getIngredient().isEmpty())
                    fluidIngredients.add(ingr);
            }
        }

        // Parsing item results
        if(GsonHelper.isValidNode(json, "results")) {
            var resultObj = json.get("results");
            if(resultObj.isJsonArray()) {
                var jsonResults = resultObj.getAsJsonArray();
                if(jsonResults != null)
                for(var element : jsonResults) {
                    var jObj = element.getAsJsonObject();
                    if(element.isJsonObject() && jObj != null)
                        results.add(ProcessingResult.fromJson(jObj));
                }
            }else if(resultObj.isJsonObject()) {
                var jsonResults = resultObj.getAsJsonObject();
                if(jsonResults != null)
                    results.add(ProcessingResult.fromJson(jsonResults));
            }
        }

        // Parsing Fluid results
        if(GsonHelper.isValidNode(json, "fluid_results")) {
            var fluidResultObj = json.get("fluid_results");
            if(fluidResultObj.isJsonArray()) {
                var jsonFluidResults = fluidResultObj.getAsJsonArray();
                if(jsonFluidResults != null)
                for(var element : jsonFluidResults) {
                    var jObj = element.getAsJsonObject();
                    if(element.isJsonObject() && jObj != null)
                        fluidResults.add(FluidHelper.deserializeFluid(jObj));
                }
            }else if(fluidResultObj.isJsonObject()) {
                var jsonFluidResults = fluidResultObj.getAsJsonObject();
                if(jsonFluidResults != null)
                    fluidResults.add(FluidHelper.deserializeFluid(jsonFluidResults));
            }
        }

        builder.withIngredients(ingredients)
            .withItemOutputs(results)
            .withFluidIngredients(fluidIngredients)
            .withFluidOutputs(fluidResults);

        builder.duration(GsonHelper.getAsInt(json, "processingTime", 0));

		T recipe = builder.build();
		recipe.readAdditional(json);
		return recipe;
    }

    /**
     * writes the recipe to the specified buffer
     * @param buffer the buffer to write the recipe to
     * @param recipe the recipe that should be written to the buffer
     */
    protected void writeToBuffer(@Nonnull FriendlyByteBuf buffer, @Nonnull T recipe) {
        var ingredients = recipe.getProcessingIngredients();
        var fluidIngredients = recipe.getFluidIngredients();
        var results = recipe.getResult();
        var fluidResults = recipe.getFluidResults();

        // Write item Ingredients
        buffer.writeVarInt(ingredients.size());
        ingredients.forEach(i -> ProcessingIngredient.toNetwork(buffer, i));

        // Write fluid Ingredients
        buffer.writeVarInt(fluidIngredients.size());
        fluidIngredients.forEach(i -> ProcessingFluidIngredient.toNetwork(buffer, i));
        
        // Write item Results
        buffer.writeVarInt(results.size());
        results.forEach(i -> ProcessingResult.toNetwork(buffer, i));

        // Write fluid Results
        buffer.writeVarInt(fluidResults.size());
		fluidResults.forEach(i -> i.writeToPacket(buffer));

        // Write processing time
        buffer.writeVarInt(recipe.getDuration());

        // Write additional data
        recipe.writeAdditional(buffer); 
    }

    /**
     * Reads a recipe from the buffer and returns it under the recipeId supplied
     * @param recipeID the id of the recipe
     * @param buffer the buffer to read the recipe data from
     * @return the recipe which was read from the buffer
     */
    protected T readFromBuffer(@Nonnull ResourceLocation recipeID, @Nonnull FriendlyByteBuf buffer) {
		NonNullList<ProcessingIngredient> ingredients = NonNullList.create();
		NonNullList<ProcessingFluidIngredient> fluidIngredients = NonNullList.create();
		NonNullList<ProcessingResult> results = NonNullList.create();
		NonNullList<FluidStack> fluidResults = NonNullList.create();

        // Reading Item Ingredient
        int size = buffer.readVarInt();
        for (int i = 0; i < size; i++)
            ingredients.add(ProcessingIngredient.fromNetwork(buffer));
            
        // Reading Fluid Ingredient
        size = buffer.readVarInt();
        for (int i = 0; i < size; i++)
            fluidIngredients.add(ProcessingFluidIngredient.fromNetwork(buffer));
            
        // Reading Item Result
        size = buffer.readVarInt();
        for (int i = 0; i < size; i++)
            results.add(ProcessingResult.fromNetwork(buffer));

        // Reading Fluid Result
        size = buffer.readVarInt();
        for(int i = 0; i < size; i++)
            fluidResults.add(FluidStack.readFromPacket(buffer));

        T recipe = new ProcessingRecipeBuilder<>(factory, recipeID)
            .withIngredients(ingredients)
            .withFluidIngredients(fluidIngredients)
            .withItemOutputs(results)
            .withFluidOutputs(fluidResults)
            .duration(buffer.readVarInt())
            .build();
        
        recipe.readAdditional(buffer);
        return recipe;
    }
    
    @Override
    public T fromJson(@Nonnull ResourceLocation location, @Nonnull JsonObject json) {
        return readFromJson(location, json);
    }

    @Override
    public @Nullable T fromNetwork(@Nonnull ResourceLocation location, @Nonnull FriendlyByteBuf buffer) {
        return readFromBuffer(location, buffer);
    }

    @Override
    public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull  T recipe) {
        writeToBuffer(buffer, recipe);
    }
    
    public ProcessingRecipeFactory<T> getFactory() {
        return factory;
    }

}
