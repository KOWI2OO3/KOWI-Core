package kowi2003.core.common.helpers;

import javax.annotation.Nonnull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import kowi2003.core.common.misc.Color;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public final class ItemHelper {
    
    /**
     * Gets the color from the itemstacks nbt data
     * @param stack The itemstack to get the color from
     * @return The color as an Vector3f
     */
    public static Color getColor(ItemStack stack) {
		CompoundTag nbt = stack.getTag();
		if(nbt == null)
			return new Color(1, 1, 1);

		float red = 1f;
		if(nbt.contains("Red"))
			red = nbt.getFloat("Red");

		float green = 1f;
		if(nbt.contains("Green"))
			green= nbt.getFloat("Green");

		float blue = 1f;
		if(nbt.contains("Blue"))
			blue = nbt.getFloat("Blue");

		return new Color(red, green, blue);
	}
	
    /**
     * Sets the color of the itemstack
     * @param stack The itemstack to set the color of
     * @param red The red value of the color
     * @param green The green value of the color
     * @param blue The blue value of the color
     * @return The itemstack with the new color
     */
	public static ItemStack setColor(ItemStack stack, float red, float green, float blue) {
		CompoundTag nbt = stack.getOrCreateTag();
		nbt.putFloat("Red", red);
		nbt.putFloat("Green", green);
		nbt.putFloat("Blue", blue);
		stack.setTag(nbt);
		return stack;
	}

    /**
     * Sets the color of the itemstack
     * @param stack The itemstack to set the color of
     * @param color The color to set to the item
     * @return The itemstack with the new color
     */
	public static ItemStack setColor(ItemStack stack, Color color) {
		return setColor(stack, color.red(), color.green(), color.blue());
	}

	/**
	 * Gets the energy from the itemstacks nbt data
	 * @param stack The itemstack to get the energy from
	 * @return The energy as an int
	 */
	public static ItemStack setEnergy(ItemStack stack, int energy)
	{
		CompoundTag nbt = stack.getOrCreateTag();
		nbt.putInt("Energy", energy);
		stack.setTag(nbt);
		return stack;
	}

	/**
	 * Sets the energy of the itemstack
	 * @param stack The itemstack to set the energy of
	 * @param energy The energy value
	 * @return The itemstack with the new energy
	 */
	public static int getEnergy(ItemStack stack)
	{
		CompoundTag nbt = stack.getTag();
		if(nbt == null)
			return 0;

		return nbt.contains("Energy") ? nbt.getInt("Energy") : 0;
	}

	/**
     * Gets the resourcelocation corresponding to the air item 
     * @return ResourceLocation of air
     */
    public static ResourceLocation getItemAirLocation() {
        return ForgeRegistries.ITEMS.getDefaultKey();
    }

    /**
     * Gets the ResourceKey of the specified item or the ResourceKey of air if no ResourceKey was found for the specified item
     * @param item item to get the resourcekey from
     * @return the resourcekey of the item or of air if the resourcekey of the item couldn't be found
     */
    public static ResourceKey<Item> getResourceKey(@Nonnull Item item) {
        var optionalHolder = ForgeRegistries.ITEMS.getHolder(item);
        if(optionalHolder.isEmpty())
            return ForgeRegistries.ITEMS.getHolder(getItemAirLocation()).get().unwrapKey().get();
        return optionalHolder.get().unwrapKey().get();
    }

    /**
     * Gets the registryname or resourcelocation of the specified item or 
     * the location of air if resourcelocation of the item couldn't be found
     * @param item the item to get the resourcelocation from
     * @return the resourcelocation of the specified item or the location of air if resourcelocation of the item couldn't be found
     */
    public static ResourceLocation getRegistryName(@Nonnull Item item) {
        return getResourceKey(item).location();
    }

    /**
     * Gets an item from its resourcekey, or air if no item has been found with the specified resourcekey
     * @param key the resourcekey to get the item from
     * @return the item from the resourcekey or air
     */
    @Nonnull
    public static Item getItemFromKey(@Nonnull ResourceKey<Item> key) {
        return ForgeRegistries.ITEMS.getHolder(key).orElse(Holder.direct(Items.AIR)).get();
    }

    /**
     * Gets an item from its resourcekey, or air if no item has been found with the specified resourcekey
     * @param location the resourcekey to get the item from
     * @return the item from the resourcekey or air
     */
    @Nonnull
    public static Item getItemFromLocation(@Nonnull ResourceLocation location) {
        return ForgeRegistries.ITEMS.getHolder(location).orElse(Holder.direct(Items.AIR)).get();
    }

    /**
     * Serializes the specified item stack into json format
     * @param stack the itemstack to serialize
     * @return the json element gotten from serializing the item stack
     */
    @Nonnull
    @SuppressWarnings("null")
    public static JsonElement serializeItemStack(ItemStack stack) {
        var json = new JsonObject();
        json.addProperty("item", getRegistryName(stack.getItem()).toString());
        json.addProperty("count", stack.getCount());
        if(stack.hasTag())
            json.add("nbt", JsonParser.parseString(stack.getTag().toString()));
        return json;
    }

    
    /**
     * Deserializes the json object into an item stack
     * @param json the json object to get the item stack from
     * @return the deserialized item stack or an empty item stack if it couldn't deserialize it
     */
    @Nonnull
    @SuppressWarnings("null")
    public static ItemStack deserializeItemStack(@Nonnull JsonObject json) {
        var itemId = GsonHelper.getAsString(json, "item", getItemAirLocation().toString());
        int count = GsonHelper.getAsInt(json, "count", 1);
        var stack = new ItemStack(getItemFromLocation(new ResourceLocation(itemId)), count);
        
        if(GsonHelper.isValidNode(json, "nbt")) {
            try {
                JsonElement element = json.get("nbt");
                var stringTag = 
                    element.isJsonObject() ? 
                    new Gson().toJson(element) :
                    GsonHelper.convertToString(element, "nbt");
                if(stringTag != null)
                    stack.setTag(TagParser.parseTag(stringTag));
            }catch(CommandSyntaxException ex) {}
        }

        return stack;
    }

}
