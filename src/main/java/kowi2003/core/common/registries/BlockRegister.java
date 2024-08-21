package kowi2003.core.common.registries;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegister implements IRegistry {
    
    /**
     * Access to the forge register to add blocks to the game
     */
    private final DeferredRegister<Block> BLOCK_REGISTRY;
    private final ItemRegister ITEM_REGISTER;

    public BlockRegister(String modId, ItemRegister itemRegister) {
        BLOCK_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, modId);
        ITEM_REGISTER = itemRegister;
    }

    /**
     * register a block without adding it to an creative tab
     * @param <T> the block type
     * @param id the id of the item, note only lower case letters and dots and dasses '_' allowed
     * @param block the block to add as a supplier
     * @return the registry object for this block
     */
    public <T extends Block> RegistryObject<T> register(String id, final Supplier<? extends T> block) {
        return register(id, null, block);
    }

    /**
     * register and adds the block them to the specified creative tab
     * @param <T> the block type
     * @param id the id of the item, note only lower case letters and dots and dasses '_' allowed
     * @param tab the tab to add the item to, null to not add it to a creative tab
     * @param block the block to add as a supplier
     * @return the registry object for this block
     */
    public <T extends Block> RegistryObject<T> register(String id, final @Nullable Supplier<ResourceKey<CreativeModeTab>> tab, final Supplier<? extends T> block) {
        return register(id, tab, block, b -> new BlockItem(b, new Item.Properties()));
    }

    /**
     * register and adds the block them to the specified creative tab
     * @param <T> the block type
     * @param id the id of the item, note only lower case letters and dots and dasses '_' allowed
     * @param tab the tab to add the item to, null to not add it to a creative tab
     * @param block the block to add as a supplier
     * @param blockItemFunction the function to use to get the item from the block
     * @return the registry object for this block
     */
    public <T extends Block> RegistryObject<T> register(String id, final @Nullable Supplier<ResourceKey<CreativeModeTab>> tab, final Supplier<? extends T> block, Function<T, BlockItem> blockItemFunction)
    {
        RegistryObject<T> result = BLOCK_REGISTRY.register(id, block);
        ITEM_REGISTER.register(id, () -> blockItemFunction.apply(result.get()));
        return result;
    }

    /**
     * register the blocks
     * @param eventBus the event bus to register the blocks to
     */
    public void register(IEventBus eventBus) {
        BLOCK_REGISTRY.register(eventBus);
    }

}
