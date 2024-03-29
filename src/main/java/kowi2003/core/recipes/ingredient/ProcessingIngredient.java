package kowi2003.core.recipes.ingredient;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.google.gson.JsonElement;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * A special kind of ingredient used by processing recipes which allow for multiple items to be consumed on a single recipe
 * 
 * @author KOWI2003
 */
public class ProcessingIngredient implements Predicate<ItemStack> {

    @Nonnull
    public static final ProcessingIngredient EMPTY = new ProcessingIngredient(Ingredient.EMPTY, 0);

    private Ingredient ingredient;
    private int count;

    /**
     * Creates a new processing ingredient with a count of 1
     * @param ingredient the ingredient to make the processing ingredient from
     */
    public ProcessingIngredient(Ingredient ingredient) {
		this(ingredient, 1);
	}
	
    /**
     * Creates a new processing ingredient from the ingredient and count supplied
     * @param ingredient the ingredient to make the processing ingredient from
     * @param count the count which is required of the given ingredient
     */
	public ProcessingIngredient(Ingredient ingredient, int count) {
		this.ingredient = ingredient;
		this.count = count;
	}

    /**
     * Gets the minecraft ingredient, note that this does not include the count
     * @return the minecraft ingredient
     */
    public Ingredient getIngredient() {
		return ingredient;
	}
	
    /**
     * gets the count of the ingredient
     * @return the count of the ingredient
     */
	public int getCount() {
		return count;
	}

    /**
     * checks to see if the itemstack given is valid for this ingredient.
     * Note that this tests both for the normal ingredient and for the count to be atleast equal to the count of the processing ingredient
     */
    @Override
    public boolean test(ItemStack stack) {
        return ingredient.test(stack) && stack.getCount() >= count;
    }

    /**
     * serializes this processing ingredient to a json object
     * @return the json element gotten from serializing this ingredient
     */
    public JsonElement toJson() {
        return toJson(this);
    }

    /**
     * serializes a processing ingredient to a json object
     * @param ingredient the ingredient to serialize
     * @return the json element gotten from serializing the ingredient
     */
    public static JsonElement toJson(@Nonnull ProcessingIngredient ingredient) {
        var json = ingredient.getIngredient().toJson().getAsJsonObject();
        json.addProperty("count", ingredient.getCount());
        return json;
    }

    /**
     * deserializes a processing ingredient from the json supplied
     * @param json the json element to get the processing ingredient from
     * @return a deserialized processing ingredient
     */
    @Nonnull 
    public static ProcessingIngredient fromJson(@Nonnull JsonElement json) {
        var ingredient = (json.isJsonObject() && json.getAsJsonObject().has("values")) ? Ingredient.fromJson(json.getAsJsonObject().get("values")) : Ingredient.fromJson(json);
        int count = json.isJsonObject() && json.getAsJsonObject().has("count") ? json.getAsJsonObject().get("count").getAsInt() : 1;

        return new ProcessingIngredient(ingredient, count);
    }

    /**
     * serializes the ingredient to a byte buffer to be send across the network
     * @param buffer the byte buffer to write the ingredient to
     */
    public static void toNetwork(@Nonnull FriendlyByteBuf buffer, ProcessingIngredient ingredient) {
        ingredient.getIngredient().toNetwork(buffer);
        buffer.writeInt(ingredient.getCount());
    }

    /**
     * gets the ingredient from the byte buffer
     * 
     * This does require the buffer to contain the ingredient as its very next entry
     * @param buffer the byte buffer to get the ingredient from
     * @return the ingredient gotton from the buffer
     */
    @Nonnull 
    public static ProcessingIngredient fromNetwork(@Nonnull FriendlyByteBuf buffer) {
        var ingredient = Ingredient.fromNetwork(buffer);
        int count = buffer.readInt();
        return new ProcessingIngredient(ingredient, count);
    }
    
}
