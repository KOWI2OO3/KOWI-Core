package kowi2003.core.recipes.ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import kowi2003.core.recipes.ingredient.FluidValues.DirectFluidValue;
import kowi2003.core.recipes.ingredient.FluidValues.DynamicFluidValue;
import kowi2003.core.recipes.ingredient.FluidValues.FluidValue;
import kowi2003.core.recipes.ingredient.FluidValues.TagFluidValue;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * An predicate Fluidstack which can define whether a fluidstack is valid or not based on the supplied fluidstacks and/or tags
 * defined in recipe json files. implemented like the item ingredient
 * 
 * @author KOWI2003 
 */
public class FluidIngredient implements Predicate<FluidStack> {

    @Nonnull 
    public static final FluidIngredient EMPTY = new FluidIngredient(Stream.empty());

    private final FluidValue[] values;
    @Nullable
    private FluidStack[] fluidStacks;

    protected FluidIngredient(Stream<? extends FluidValue> values) {
        this.values = values.toArray(size -> new FluidValue[size]);
    }

    /**
     * dissolves the values into the internal fluid list
     * this makes the entire ingredient definable by a single list
     */
    private void dissolve() {
        if(fluidStacks == null) {
            fluidStacks = 
            Arrays.stream(this.values)
            .flatMap(value -> value.getFluids().stream())
            .distinct()
            .toArray(size -> new FluidStack[size]);
        }
    }

    @Override
    @SuppressWarnings("null")
    public boolean test(FluidStack stack) {
        if(stack == null) return false;

        this.dissolve();
        if(fluidStacks == null || fluidStacks.length == 0)
            return stack.isEmpty();
        
        for (var fluidStack : fluidStacks) {
            if(fluidStack.getFluid() == stack.getFluid())
                return true;
        }
        return false;
    }

    /**
     * Serializes the ingredient to the byte buffer to be sent across the network
     * @param buffer the buffer to write the ingredient to
     */
    public final void toNetwork(@Nonnull FriendlyByteBuf buffer) {
        this.dissolve();
        buffer.writeCollection(Arrays.asList(this.fluidStacks), FriendlyByteBuf::writeFluidStack);
    }

    /**
     * deserializes the ingredient from the byte buffer
     * used in transporting the ingredient over the network
     * @param buffer the buffer 
     * @return the fluid ingredient as gotten from the buffer
     */
    @Nonnull 
    public static FluidIngredient fromNetwork(@Nonnull FriendlyByteBuf buffer) {
        var size = buffer.readVarInt();
        if(size == -1) return getIngredient(buffer);
        return fromValues(Stream.generate(() -> new DirectFluidValue(buffer.readFluidStack())).limit(size));
    }

    /**
     * Creates an json element from this ingredient
     * @return the json element holding the data of the ingredient
     */
    @Nonnull
    public JsonElement toJson() {
        if(this.values.length == 1)
            return this.values[0].serialize();

        var array = new JsonArray();
        for(var value : this.values)
            array.add(value.serialize());
        
        return array;
    }

    /**
     * parses an fluid ingredient from an json element containing the correct format and data
     * @param json the json element to parse
     * @return the fluid ingredient as parsed from the json
     */
    @Nonnull
    public static FluidIngredient fromJson(@Nonnull JsonElement json) {
        if(json == null || json.isJsonNull())
            throw new JsonSyntaxException("Fluid json cannot be null");
        
        return getIngredient(json);
    } 

    @SuppressWarnings("null")
    public boolean isEmpty() {
        return this.values.length == 0 && (this.fluidStacks == null || this.fluidStacks.length == 0);
    }

    @Nonnull 
    public static FluidIngredient of() {
        return EMPTY;
    }

    @Nonnull 
    public static FluidIngredient of(Fluid... fluids) {
        return of(Arrays.stream(fluids).map(f -> new FluidStack(f, 1)));
    }

    @Nonnull 
    public static FluidIngredient of(FluidStack... fluids) {
        return of(Arrays.stream(fluids));
    }

    @Nonnull 
    public static FluidIngredient of(Stream<? extends FluidStack> stream) {
        return fromValues(stream.filter(f -> !f.isEmpty()).map(DirectFluidValue::new));
    }

    @Nonnull 
    public static FluidIngredient of(TagKey<Fluid> tag) {
        return fromValues(Stream.of(new TagFluidValue(tag)));
    }

    /**
     * creates the ingredient from the values, or returns the empty ingredient if it is empty
     * @param stream the stream containing the values containing the tests for the ingredient
     * @return the fluid ingredient created according to the fluid values
     */
    @Nonnull 
    public static FluidIngredient fromValues(Stream<? extends FluidValue> stream) {
        var ingredient = new FluidIngredient(stream);
        return ingredient.values.length == 0 ? EMPTY : ingredient;
    }

    /**
     * gets an fluid ingredient from the buffer supplied, mainly used in network applications
     * @param buffer the buffer to get the fluid ingredient from
     * @return the fluid ingredient gotten from the buffer or an empty fluid ingredient if it couldn't get it from the buffer 
     */
    @Nonnull
    private static FluidIngredient getIngredient(FriendlyByteBuf buffer) {
        var stack = buffer.readFluidStack();
        return stack.isEmpty() ? EMPTY : of(stack);
    }

    /**
     * gets an fluid ingredient parsed fomr the json element 
     * @param json the json to get the fluid ingredient from
     * @return the parsed fluid ingredient
     * 
     * @throws JsonSyntaxException can be thrown if the json format is incorrect
     * @throws JsonParseException if the something went wrong with actually parsing the json
     */
    @Nonnull
    private static FluidIngredient getIngredient(@Nonnull JsonElement json) {
        if(json == null || json.isJsonNull())
            throw new JsonParseException("Json cannot be null");

        // Checking for an array and if so then handle the array of ingredients
        if(json.isJsonArray()) {
            var ingredients = new ArrayList<FluidIngredient>();
            
            json.getAsJsonArray().forEach(e -> ingredients.add(getIngredient(e)));

            if(ingredients.size() == 0)
                throw new JsonSyntaxException("Fluid array cannot be empty, at least one fluid must be defined");
            
            if(ingredients.size() == 1)
                return ingredients.get(0);

            return merge(ingredients);
        }

        return fromValues(Stream.of(valueFromJson(json)));
    }

    /**
     * Parses the fluid value from the json object supplied. 
     * creating a direct fluid value or an tag fluid value depending on the json structure 
     * 
     * @param json the json to parse
     * @return the fluid value that has been created
     * 
     * @throws JsonParseException can be thrown if the json format is incorrect
     */
    @Nonnull
    public static FluidValue valueFromJson(@Nonnull JsonElement element) {
        if(element == null || element.isJsonNull())
            throw new JsonParseException("Json cannot be null");

        if(element.isJsonPrimitive() && element.getAsJsonPrimitive().isString())
            return new DynamicFluidValue(element.getAsString());

        if(!element.isJsonObject())
            throw new JsonParseException("Json must be an primitive string or an json object");

        var json = element.getAsJsonObject(); 

        if(json.has("fluid") && json.has("tag"))
            throw new JsonParseException("An fluid ingredient entry is either a tag or an fluid, not both");

        // Parsing Direct Fluid Value
        else if(json.has("fluid")) {
            final var fluidId = GsonHelper.getAsString(json, "fluid");
            if(fluidId == null || fluidId.isEmpty()) throw new JsonSyntaxException("Unknown fluid '" + fluidId + "'");

            var fluid = ForgeRegistries.FLUIDS.getHolder(new ResourceLocation(fluidId))
                .orElseThrow(() -> new JsonSyntaxException("Unknown fluid '" + fluidId + "'"));
            return new DirectFluidValue(fluid.get());
        }

        // Parsing Tag Value
        else if(json.has("tag")) {
            final var tagId = GsonHelper.getAsString(json, "tag");
            if(tagId == null || tagId.isEmpty()) throw new JsonSyntaxException("tag cannot be null or empty");

            var location = new ResourceLocation(tagId);
            var tag = TagKey.create(Registries.FLUID, location);
            return new TagFluidValue(tag);
        }

        throw new JsonParseException("An fluid ingredient entry needs either a tag or an fluid");
    }

    /**
     * Merges multiple ingredients into one
     * @param ingredients the ingredients to merge
     * @return the merged ingredient
     */
    public static FluidIngredient merge(Collection<FluidIngredient> ingredients) {
        return fromValues(ingredients.stream().flatMap(i -> Arrays.stream(i.values)));
    }
}
