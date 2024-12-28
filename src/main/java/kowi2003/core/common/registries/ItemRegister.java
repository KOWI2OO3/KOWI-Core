package kowi2003.core.common.registries;

import java.util.function.Supplier;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegister implements IRegistry{
    
    /**
     * Access to the forge register to add items to the game
     */
    private final DeferredRegister<Item> ITEM_REGISTRY;
    
    public ItemRegister(String modId) {
        ITEM_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, modId);
    }

    /**
     * register items
     * @param <T> the item type
     * @param name the id of the item, note only lower case letters and dots and dasses '_' allowed
     * @param item the item to add as a supplier 
     * @return the registry object for this item
     */
    public <T extends Item> RegistryObject<Item> register(String name, final Supplier<T> item) {
        return ITEM_REGISTRY.register(name, item);
    }

    /**
     * register the items to the event bus
     * @param eventBus the event bus to register the items to
     */
    public void register(IEventBus eventBus) {
        ITEM_REGISTRY.register(eventBus);
    }

}
