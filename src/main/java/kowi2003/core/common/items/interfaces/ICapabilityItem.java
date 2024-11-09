package kowi2003.core.common.items.interfaces;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public interface ICapabilityItem {

	void getherCapabilities(ItemStack stack, AttachCapabilitiesEvent<ItemStack> event);
	
}
