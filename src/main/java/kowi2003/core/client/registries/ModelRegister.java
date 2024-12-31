package kowi2003.core.client.registries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import kowi2003.core.Core;
import kowi2003.core.client.model.IModel;
import kowi2003.core.client.model.IModelType;
import kowi2003.core.client.model.JsonModel;
import kowi2003.core.client.model.WavefontModel;
import kowi2003.core.common.registries.IRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.client.model.obj.ObjModel;
import net.minecraftforge.client.model.obj.ObjModel.ModelSettings;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModelRegister implements IRegistry
{
    private static final String WAVEFONT_EXTENSION = ".obj";

    final List<ResourceLocation> LOCATIONS = new ArrayList<>();
    final Map<ResourceLocation, ModelSettings> SETTINGS = new HashMap<>();
    final Map<ResourceLocation, IModel> registeredModels = new HashMap<>();

    private String modid = null;
    
    public ModelRegister(String modid) {
        this.modid = modid;
    }

    public IModelType register(String path) {
        return register(modid != null ? new ResourceLocation(modid, path) : new ResourceLocation(path));
    }

    public IModelType register(ResourceLocation location) 
    {   
        if(location.getPath().endsWith(WAVEFONT_EXTENSION))
            return registerWavefont(location, null);

        LOCATIONS.add(location);
        return () -> registeredModels.get(location);
    }

    public IModelType registerWavefont(ResourceLocation location, @Nullable ModelSettings settings) 
    {   
        LOCATIONS.add(location);
        SETTINGS.put(location, settings);
        return () -> registeredModels.get(location);
    }

    public void register(IEventBus eventBus)
    {
        eventBus.addListener(this::registerAdditional);
        eventBus.addListener(this::retrieveModels);
    }

    @SuppressWarnings("null")
    private void registerWavefontModel(ResourceLocation location)
    {
        var locationId = location;
        if(!SETTINGS.containsKey(location))
        {
            Core.LOGGER.error("Failed to load wavefont model: " + location + ", missing settings");
            return;
        }

        if(!location.getPath().endsWith(WAVEFONT_EXTENSION))
            location = new ResourceLocation(location.getNamespace(), location.getPath() + WAVEFONT_EXTENSION);
        
        location = new ResourceLocation(location.getNamespace(), "models/" + location.getPath());

        var settings = SETTINGS.get(location);

        if(settings == null)
            settings = new ObjModel.ModelSettings(location, true, true, false, false, null);
            
        var model = ObjLoader.INSTANCE.loadModel(settings);
        var renderable = model.bakeRenderable(StandaloneGeometryBakingContext.create(location));

        registeredModels.put(locationId, new WavefontModel(renderable));
    }


    private void registerAdditional(final ModelEvent.RegisterAdditional event) 
    {
        LOCATIONS.forEach(event::register);
    }

    private void retrieveModels(final ModelEvent.BakingCompleted event) 
    {
        var models = event.getModels();
        for (var location : LOCATIONS) 
        {
            if(SETTINGS.containsKey(location))
            {
                registerWavefontModel(location);
                continue;
            }

            registeredModels.put(location, new JsonModel(models.get(location)));
        }
    }

}
