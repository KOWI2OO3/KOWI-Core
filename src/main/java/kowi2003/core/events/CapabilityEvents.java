package kowi2003.core.events;

import kowi2003.core.Core;
import kowi2003.core.capability.item.CoreFluidHandlerItemStack;
import kowi2003.core.capability.item.ItemCapabilityProvider;
import kowi2003.core.capability.item.ItemEnergyStorage;
import kowi2003.core.capability.item.ItemItemStackHandler;
import kowi2003.core.items.interfaces.IAttachItemCapability;
import kowi2003.core.items.interfaces.IItemEnergyStorage;
import kowi2003.core.items.interfaces.IItemFluidContainer;
import kowi2003.core.items.interfaces.IItemInventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.items.IItemHandler;

/**
 * Handling of the forge capability attching process.
 * This class handles all of the automatic capability attaching that is done in the core
 * 
 * @author KOWI2003 
 */
@Mod.EventBusSubscriber(bus = Bus.FORGE, modid = Core.MODID)
public class CapabilityEvents {
    
    @SubscribeEvent
    public static void attachItemStackCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();

        // Attaching Energy capability
        if(stack.getItem() instanceof IItemEnergyStorage storage) {
            event.addCapability(new ResourceLocation(Core.MODID, "energy"), 
                new ItemCapabilityProvider<IEnergyStorage>(ForgeCapabilities.ENERGY, 
                    new ItemEnergyStorage(storage, stack)));
        }

        // Attaching Inventory capability
        if(stack.getItem() instanceof IItemInventory inventory) {
            event.addCapability(new ResourceLocation(Core.MODID, "inventory"), 
                new ItemCapabilityProvider<IItemHandler>(ForgeCapabilities.ITEM_HANDLER, 
                    new ItemItemStackHandler(inventory.getSlotCount(), stack)));
        }

        // Attaching Fluid capability
        if(stack.getItem() instanceof IItemFluidContainer container) {
            event.addCapability(new ResourceLocation(Core.MODID, "fluid_container"), 
                new ItemCapabilityProvider<IFluidHandlerItem>(ForgeCapabilities.FLUID_HANDLER_ITEM, 
                    new CoreFluidHandlerItemStack(stack, container.getContainerCapacity(), container::isFluidValid)));
        }

        // Forwarding capability attaching to items that allow it
        if(stack.getItem() instanceof IAttachItemCapability capabilityItem)
            capabilityItem.gatherCapabilities(stack, event);
    }

}
