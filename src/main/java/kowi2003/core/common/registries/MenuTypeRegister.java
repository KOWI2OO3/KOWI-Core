package kowi2003.core.common.registries;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuTypeRegister implements IRegistry {

    /**
     * Access to the forge register to add menu types to the game
     */
    private final DeferredRegister<MenuType<?>> MENU_TYPES;

    public MenuTypeRegister(String modId) {
        MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, modId);
    }

    /**
     * register a container
     * @param <T> the container type
     * @param name the name of the container type
     * @param containerFactory the factory to create the container (from a byte buffer)
     * @return the registry object for this container
     */
    public <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String name, IContainerFactory<T> containerFactory) {
        return MENU_TYPES.register(name, () -> IForgeMenuType.create(containerFactory));
    }

    @Override
    public void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
