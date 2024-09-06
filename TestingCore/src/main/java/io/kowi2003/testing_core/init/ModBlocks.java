package io.kowi2003.testing_core.init;

import io.kowi2003.testing_core.TestingCore;
import kowi2003.core.common.blocks.DefaultBlock;
import kowi2003.core.common.blocks.HorizontalBlock;
import kowi2003.core.common.blocks.OrientableBlock;
import kowi2003.core.common.blocks.RotatableBlock;
import kowi2003.core.common.blocks.VerticalBlock;
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
    public static final RegistryObject<Block> EXAMPLE_HORIZONTAL_BLOCK = BLOCKS.register("example_block_horizontal", () -> new HorizontalBlock(
        Block.Properties.of()
        .strength(3.0F, 3.0F),
        Shapes.box(0.25, 0.25, 0, 0.75, 0.75, 1)   // Rotatable shape for the horizontal block
    ));

    // Example of using the vertical block to easily add a block that can be set upside down (like slabs)
    public static final RegistryObject<Block> EXAMPLE_VERTICAL_BLOCK = BLOCKS.register("example_block_vertical", () -> new VerticalBlock(
        Block.Properties.of()
        .strength(3.0F, 3.0F),
        Shapes.box(0, 0, 0, 1, 0.5, 1)   // Rotatable shape for the vertical block
    ));

    // Example of using the vertical block to easily add a block that can be set upside down (like slabs)
    public static final RegistryObject<Block> EXAMPLE_ROTATABLE_BLOCK = BLOCKS.register("example_block_rotatable", () -> new RotatableBlock(
        Block.Properties.of()
        .strength(3.0F, 3.0F),
        Shapes.box(0.09375, 0, 0.09375, 0.90625, 0.125, 0.90625)   // Rotatable shape for the rotatable block
    ));

    // Example of using the orientable block to easily add a block that can be set in any direction and upside down (like stairs)
    public static final RegistryObject<Block> EXAMPLE_ORIENTABLE_BLOCK = BLOCKS.register("example_block_orientable", () -> new OrientableBlock(
        Block.Properties.of()
        .strength(3.0F, 3.0F),
        Shapes.box(0, 0, 0, .25, 0.25, 1)   // Rotatable shape for the orientale block
    ));

    // Register the blocks to the event bus, similar to how you would do it normally
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
