package io.kowi2003.testing_core;

import io.kowi2003.testing_core.init.ModBlocks;
import io.kowi2003.testing_core.init.ModCreativeTabs;
import io.kowi2003.testing_core.init.ModItems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
/**
 * The main class for the mod, where everything is initialized
 * The testing core mod is meant as an example of how to use the KOWI Core mod 
 * and its used for me to test the core mod with
 * 
 * @author KOWI2003
 */
@Mod(TestingCore.MODID)
public class TestingCore
{
    // The modid definition of the mod used as a reference
    public static final String MODID = "testing_core";
    
    public TestingCore()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        // Registering blocks and items for the mod just like you would do normally
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

// More random forge stuff setup to be used in the future if needed

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }
}
