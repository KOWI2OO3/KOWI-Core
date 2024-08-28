package io.kowi2003.testing_core.init;

import io.kowi2003.testing_core.TestingCore;
import kowi2003.core.common.blocks.DefaultBlock;
import kowi2003.core.common.blocks.HorizontalBlock;
import kowi2003.core.common.registries.BlockRegister;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;

/**
 * An example of a mod block register class using the KOWI Core's BlockRegister class
 */
public class ModBlocks {
    
    // Defining the block register as a static final field
    static final BlockRegister BLOCKS = new BlockRegister(TestingCore.MODID, ModItems.ITEMS);

    // Register blocks here 

    // Example of using the default block to easily add a simple block without the hassle of creating a new class
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new DefaultBlock(Block.Properties.of().strength(3.0F, 3.0F)));

    // Example of using the horizontal block to easily add a block that can be rotated horizontally
    public static final RegistryObject<Block> EXAMPLE_ROTATABLE_BLOCK = BLOCKS.register("example_rotatable_block", () -> new HorizontalBlock(
        Block.Properties.of()
        .strength(3.0F, 3.0F),
        Shapes.box(0.0, 0.0, -.5f, 1.0f, 1.0f, 1.5f)   // Rotatable block shape for the horizontal block
    ));

    // Register the blocks to the event bus, similar to how you would do it normally
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
