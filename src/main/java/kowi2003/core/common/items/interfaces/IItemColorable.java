package kowi2003.core.common.items.interfaces;

import kowi2003.core.common.helpers.ItemHelper;
import kowi2003.core.common.misc.Color;
import net.minecraft.world.item.ItemStack;

public interface IItemColorable {

	default Color getRGB(ItemStack stack, int tintindex) {
		return getColor(stack);
	}
	
	default Color getColor(ItemStack stack)
	{
		return ItemHelper.getColor(stack);
	}
	
	default ItemStack setColor(ItemStack stack, float red, float green, float blue) {
		return ItemHelper.setColor(stack, red, green, blue);
	}

	default ItemStack setColor(ItemStack stack, Color color) {
		return ItemHelper.setColor(stack, color);
	}
	
}
