package kowi2003.core.recipes.ingredient;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.google.gson.JsonElement;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;

/**
 * A special kind of fluid ingredient used by processing recipes which allow for specifying the amount of mb to be consumed on a single recipe
 * 
 * @author KOWI2003
 */
public class ProcessingFluidIngredient implements Predicate<FluidStack> {

    @Nonnull
    public static final ProcessingFluidIngredient EMPTY = new ProcessingFluidIngredient(FluidIngredient.EMPTY);

    private FluidIngredient ingredient;
    private int count;
    
    /**
     * Creates a new processing fluid ingredient with a count of 1
     * @param ingredient the fluid ingredient to make the processing fluid ingredient from
     */
    public ProcessingFluidIngredient(FluidIngredient ingredient) {
		this(ingredient, 1);
	}
	
    /**
     * Creates a new processing fluid ingredient from the ingredient and count supplied
     * @param ingredient the fluid ingredient to make the processing fluid ingredient from
     * @param count the count which is required of the given fluid ingredient (in mb)
     */
	public ProcessingFluidIngredient(FluidIngredient ingredient, int count) {
		this.ingredient = ingredient;
		this.count = count;
	}

    /**
     * Gets the fluid ingredient, note that this does not include the count
     * @return the fluid ingredient
     */
    public FluidIngredient getIngredient() {
		return ingredient;
	}
	
    /**
     * gets the count of the fluid ingredient
     * @return the count of the fluid ingredient
     */
	public int getCount() {
		return count;
	}

    /**
     * checks to see if the fluidstack given is valid for this fluid ingredient.
     * Note that this tests both for the normal fluid ingredient and for the count to be atleast equal to the count of the processing fluid ingredient
     */
    @Override
    public boolean test(FluidStack fluid) {
        return ingredient.test(fluid) && fluid.getAmount() >= count;
    }

    /**
     * serializes this processing fluid ingredient to a json object
     * @return the json element gotten from serializing this fluid ingredient
     */
    public JsonElement toJson() {
        return toJson(this);
    }

    /**
     * serializes a processing fluid fluid ingredient to a json object
     * @param ingredient the ingredient to serialize
     * @return the json element gotten from serializing the fluid ingredient
     */
    public static JsonElement toJson(@Nonnull ProcessingFluidIngredient ingredient) {
        var json = ingredient.getIngredient().toJson().getAsJsonObject();
        json.addProperty("count", ingredient.getCount());
        return json;
    }

    /**
     * deserializes a processing fluid ingredient from the json supplied
     * @param json the json element to get the processing fluid ingredient from
     * @return a deserialized processing fluid ingredient
     */
    @Nonnull
    @SuppressWarnings("null")
    public static ProcessingFluidIngredient fromJson(@Nonnull JsonElement json) {
        var ingredient = (json.isJsonObject() && json.getAsJsonObject().has("values")) ? FluidIngredient.fromJson(json.getAsJsonObject().get("values")) : FluidIngredient.fromJson(json);
        int count = json.isJsonObject() && json.getAsJsonObject().has("count") ? json.getAsJsonObject().get("count").getAsInt() : 1;

        return new ProcessingFluidIngredient(ingredient, count);
    }

    /**
     * serializes the fluid ingredient to a byte buffer to be send across the network
     * @param buffer the byte buffer to write the fluid ingredient to
     */
    public static void toNetwork(@Nonnull FriendlyByteBuf buffer, ProcessingFluidIngredient ingredient) {
        ingredient.getIngredient().toNetwork(buffer);
        buffer.writeInt(ingredient.getCount());
    }

    /**
     * gets the fluid ingredient from the byte buffer
     * 
     * This does require the buffer to contain the fluid ingredient as its very next entry
     * @param buffer the byte buffer to get the ingredient from
     * @return the fluid ingredient gotton from the buffer
     */
    @Nonnull 
    public static ProcessingFluidIngredient fromNetwork(@Nonnull FriendlyByteBuf buffer) {
        var ingredient = FluidIngredient.fromNetwork(buffer);
        int count = buffer.readInt();
        return new ProcessingFluidIngredient(ingredient, count);
    }
    
}
