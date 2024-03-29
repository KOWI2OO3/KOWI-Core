package kowi2003.core.items.interfaces;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;

/**
 * This allows an item to define its own capabilities by forwarding the event to the item class.
 * this is an easy and straight forward way to add capabilities to your items.
 * 
 * The core handles all of the calling and event handling and you can define your own capabilities for your item
 * 
 * @author KOWI2003
 */
public interface IAttachItemCapability {
    
    /**
     * This method is called on the event of attaching capabilities onto your item. Its a forwarded event which means you should add the capability yourself.
     * adding the capability can be done using the event and the @see{kowi2003.core.capability.item.ItemCapabilityProvider ItemCapabilityProvider} of the core or a custom capability provider 
     * @param stack the item stack to attach the capability to
     * @param event the event that attaches the capabilities
     */
	void gatherCapabilities(ItemStack stack, AttachCapabilitiesEvent<ItemStack> event);

}
