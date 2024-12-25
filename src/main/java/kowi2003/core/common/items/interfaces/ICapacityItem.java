package kowi2003.core.common.items.interfaces;

import net.minecraft.world.item.ItemStack;

public interface ICapacityItem {

	int getMaxCapacity(ItemStack stack);
	
	int getCapacity(ItemStack stack);
	
}
