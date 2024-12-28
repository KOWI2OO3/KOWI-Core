package io.kowi2003.testing_core.init;

import io.kowi2003.testing_core.TestingCore;
import kowi2003.core.common.registries.ItemRegister;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;

/**
 * An example of a mod item register class using the KOWI Core's ItemRegister class
 */
public class ModItems {
    
    // Defining the item register as a static final field
    static final ItemRegister ITEMS = new ItemRegister(TestingCore.MODID);
    
    // Regitster items here
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties()));

    // Register the items to the event bus, similar to how you would do it normally
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
