package io.kowi2003.testing_core.init;

import io.kowi2003.testing_core.TestingCore;
import kowi2003.core.common.registries.CreativeTabRegister;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    
    // Defining the creative tab register as a static final field
    static final CreativeTabRegister CREATIVE_TABS = new CreativeTabRegister(TestingCore.MODID);

    // Register creative tabs here
    // Example of using the register to easily add a creative tab
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_TABS.register("example_tab", () -> CreativeModeTab.builder()
        .icon(() -> new ItemStack(Blocks.STONE.asItem()))                                   // Defining the icon
        .title(Component.translatable("creative_tab." + TestingCore.MODID + ".base"))       // Defining the name
        .displayItems((ItemDisplayParameters, output) -> {
                output.accept(new ItemStack(ModBlocks.EXAMPLE_BLOCK.get()));
                output.accept(new ItemStack(ModBlocks.EXAMPLE_HORIZONTAL_BLOCK.get()));
                output.accept(new ItemStack(ModBlocks.EXAMPLE_VERTICAL_BLOCK.get()));
                output.accept(new ItemStack(ModBlocks.EXAMPLE_ROTATABLE_BLOCK.get()));
                output.accept(new ItemStack(ModBlocks.EXAMPLE_ORIENTABLE_BLOCK.get()));
                output.accept(new ItemStack(ModBlocks.EXAMPLE_CONNECTABLE_BLOCK.get()));
                output.accept(new ItemStack(ModItems.EXAMPLE_ITEM.get()));
            })
        .build());

    // Register the creative tabs to the event bus, similar to how you would do it normally
    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
