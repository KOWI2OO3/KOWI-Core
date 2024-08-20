package kowi2003.core;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import kowi2003.core.client.init.ClientSetup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Core.MODID)
public class Core 
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "kowi_core";

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    
    public Core()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onClientSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        
    }
    
    private void onClientSetup(final FMLClientSetupEvent event)
    {
    	ClientSetup.onSetupClient();
    }

}
