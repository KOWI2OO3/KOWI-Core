package kowi2003.core.common.registries;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTabRegister implements IRegistry {
    
    private final DeferredRegister<CreativeModeTab> CREATIVE_TAB_REGISTRY;

    public CreativeTabRegister(String modId) {
        CREATIVE_TAB_REGISTRY  = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, modId);
    }

    /**
     * register a creative tab
     * @param name the name of the creative tab
     * @param tab the creative tab to add as a supplier
     * @return the registry object for this creative tab
     */
    public RegistryObject<CreativeModeTab> register(String name, Supplier<CreativeModeTab> tab) {
        return CREATIVE_TAB_REGISTRY.register(name, tab);
    }

    @Override
    public void register(IEventBus eventBus) {
        CREATIVE_TAB_REGISTRY.register(eventBus);
    }
}

