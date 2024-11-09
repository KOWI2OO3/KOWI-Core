package kowi2003.core.common.helpers;

import kowi2003.core.common.misc.Color;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

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

}
