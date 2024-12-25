package kowi2003.core.common.events;

import kowi2003.core.Core;
import kowi2003.core.common.capabilities.ItemCapabilityProvider;
import kowi2003.core.common.capabilities.ItemEnergyStorage;
import kowi2003.core.common.capabilities.ItemFluidHandler;
import kowi2003.core.common.capabilities.ItemItemStackHandler;
import kowi2003.core.common.items.interfaces.ICapabilityItem;
import kowi2003.core.common.items.interfaces.IItemEnergyStorage;
import kowi2003.core.common.items.interfaces.IItemFluidContainer;
import kowi2003.core.common.items.interfaces.IItemInventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;

@Mod.EventBusSubscriber(modid = Core.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityEvents {
    
    @SubscribeEvent
    public static void attachItemStackCapabilities(AttachCapabilitiesEvent<ItemStack> event)
    {
        var stack = event.getObject();
        var item = stack.getItem();
        if(item instanceof IItemEnergyStorage storage)
        {
            event.addCapability(new ResourceLocation(Core.MODID, "energy"), 
                new ItemCapabilityProvider<IEnergyStorage>(ForgeCapabilities.ENERGY, 
                    new ItemEnergyStorage(stack, storage.getMaxRecieve(), storage.getMaxExtract(), storage.getDefaultEnergy())));
        }
        if(item instanceof IItemInventory inventory)
        {
            event.addCapability(new ResourceLocation(Core.MODID, "inventory"), 
                new ItemCapabilityProvider<IItemHandler>(ForgeCapabilities.ITEM_HANDLER, 
                    new ItemItemStackHandler(stack, inventory.getSlotCount())));
        }
        if(item instanceof IItemFluidContainer container)
        {
            event.addCapability(new ResourceLocation(Core.MODID, "fluid"), 
               new ItemCapabilityProvider<IFluidHandlerItem>(ForgeCapabilities.FLUID_HANDLER_ITEM, 
                   new ItemFluidHandler(stack, container.getContainerCapacity(), container::isFluidValid)));
        }
        if(item instanceof ICapabilityItem capabilityItem)
            capabilityItem.getherCapabilities(stack, event);
    }
}
