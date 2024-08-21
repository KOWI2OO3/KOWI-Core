package io.kowi2003.testing_core.init;

import io.kowi2003.testing_core.TestingCore;
import kowi2003.core.common.blocks.DefaultBlock;
import kowi2003.core.common.registries.BlockRegister;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    
    static final BlockRegister BLOCKS = new BlockRegister(TestingCore.MODID, ModItems.ITEMS);

    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new DefaultBlock(Block.Properties.of().strength(3.0F, 3.0F)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
