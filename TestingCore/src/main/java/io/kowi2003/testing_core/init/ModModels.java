package io.kowi2003.testing_core.init;

import io.kowi2003.testing_core.TestingCore;
import kowi2003.core.client.model.IModelType;
import kowi2003.core.client.registries.ModelRegistry;
import net.minecraft.resources.ResourceLocation;

public final class ModModels {
    
    public static ModelRegistry registry = new ModelRegistry();

    public static IModelType HELI = registry.register(new ResourceLocation(TestingCore.MODID, "render/model.obj"));

}
