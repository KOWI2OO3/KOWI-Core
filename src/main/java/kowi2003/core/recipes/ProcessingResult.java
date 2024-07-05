package kowi2003.core.recipes;

import javax.annotation.Nonnull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import kowi2003.core.utils.ItemHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

/**
 * An class which allows a recipe result to contain a few extra properties.
 * 
 * These prorties define the range of amount (min and max)
 * this range will define how many of the result should be added in the recipe's output 
 * 
 * And a chance property which will define the chance (0 to 1) that this result will be included in the recipe's output  
 * 
 * @author KOWI2003
 */
public class ProcessingResult {
    
    @Nonnull
    public static final ProcessingResult EMPTY = new ProcessingResult(ItemStack.EMPTY, 1);

	private ItemStack stack;
	private int rangeMin;
	private int rangeMax;
	private float chance;

    /**
     * Creates a new processing result from an itemstack and the chance
     * @param stack the itemstack of the result
     * @param chance the chance this result comes out of the recipe (range: 0-1, 0 being never, 1 being always)
     */
    public ProcessingResult(ItemStack stack, float chance) {
        this(stack, -1, -1, chance);
    }

    /**
     * Creates a new processing result from an itemstack and the count range.
     * 
     * setting the min and max to the same value will force the output to be that amount,
     * so setting both to 2 then the output wil guaranteed be size of 2
     * @param stack the itemstack of the result
     * @param rangeMin the min amount of items as result (-1 or equal to rangeMax to disable range)
     * @param rangeMax the max amount of items as result (-1 or equal to rangeMin to disable range)
     */
    public ProcessingResult(ItemStack stack, int rangeMin, int rangeMax) {
        this(stack, rangeMin, rangeMax, 1);
    }
    
    /**
     * Creates a new processing result from an itemstack, the chance and the count range.
     * 
     * setting the min and max to the same value will force the output to be that amount,
     * so setting both to 2 then the output wil guaranteed be size of 2
     * @param stack the itemstack of the result
     * @param rangeMin the min amount of items as result (-1 or equal to rangeMax to disable range)
     * @param rangeMax the max amount of items as result (-1 or equal to rangeMin to disable range)
     * @param chance the chance this result comes out of the recipe (range: 0-1, 0 being never, 1 being always)
     */
    public ProcessingResult(ItemStack stack, int rangeMin, int rangeMax, float chance) {
        this.stack = stack;
        this.rangeMin = rangeMin;
        this.rangeMax = rangeMax;
        this.chance = chance;
    }

    /**
     * checks whether the result has a range
     * @return whether the result has a range
     */
    public boolean hasRange() {
		return rangeMin < rangeMax && (rangeMin > 0 && rangeMax > 0);
    }

    /**
     * gets the range of the recipe result
     * @return int[] containing the range where [0] = min and [1] = max
     */
    public int[] getRange() {
		return new int[] {rangeMin, rangeMax};
    }

    /**
     * gets the chance the result will be used
     * @return the chance of the result
     */
    public float getChance() {
		return chance;
	}
	
    /**
     * gets the itemstack of the recipe result
     * @return the itemstack of the result
     */
    @Nonnull
    public ItemStack getItem() {
		return stack == null ? ItemStack.EMPTY : stack;
	}

    /**
     * Serializes the specified processing result to json
     * @param result the processing result to serialized
     * @return the serialized processing result in a json element
     */
    @Nonnull
    public static JsonElement toJson(@Nonnull ProcessingResult result) {
        var json = ItemHelper.serializeItemStack(result.getItem()).getAsJsonObject();
        if(result.hasRange()) {
            var rangeJson = new JsonObject();
            rangeJson.addProperty("min", result.rangeMin);
            rangeJson.addProperty("max", result.rangeMax);
            json.add("range", rangeJson);
        }
        if(result.chance < 1)
            json.addProperty("chance", result.chance);
        return json;
    }

    /**
     * Gets a processing result from the specified json object
     * @param json the json object to get the processing result from
     * @return the processing result as gotten from the json object
     */
    public static ProcessingResult fromJson(@Nonnull JsonObject json) {
        var stack = ItemHelper.deserializeItemStack(json);
        int minRange = -1;
        int maxRange = -1;
        if(json.has("range") && json.get("range").isJsonObject()) {
            var rangeJson = json.get("range").getAsJsonObject();
            if(rangeJson != null) {
                minRange = GsonHelper.getAsInt(rangeJson, "min", -1);
                maxRange = GsonHelper.getAsInt(rangeJson, "max", -1);
            }
        }
        float chance = GsonHelper.getAsFloat(json, "chance", 1f);
        return new ProcessingResult(stack, minRange, maxRange, chance);
    }

    /**
     * Stores the processing result into the supplied buffer, to be send across the network
     * @param buffer the buffer to store the result in
     * @param result the processing result to store
     */
    public static void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull ProcessingResult result) {
        buffer.writeItem(result.getItem());
		buffer.writeVarInt(result.rangeMin);
		buffer.writeVarInt(result.rangeMax);
		buffer.writeFloat(result.chance);
    }

    /**
     * Retrieves the processing result from the supplied buffer
     * @param buffer the buffer to retrieve the result from
     * @return the processing result retrieved from the buffer
     */
    @Nonnull
    public static ProcessingResult fromNetwork(FriendlyByteBuf buffer) {
        var stack = buffer.readItem();
        var minRange = buffer.readVarInt();
        var maxRange = buffer.readVarInt();
        var chance = buffer.readFloat();
        return new ProcessingResult(stack, minRange, maxRange, chance);
    }
    
}
