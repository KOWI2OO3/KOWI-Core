package io.kowi2003.testing_core.init;

import io.kowi2003.testing_core.TestingCore;
import kowi2003.core.client.model.IModelType;
import kowi2003.core.client.registries.ModelRegister;
import net.minecraft.resources.ResourceLocation;

public final class ModModels {
    
    public static ModelRegister registry = new ModelRegister(TestingCore.MODID);

    public static IModelType HELI = registry.register(new ResourceLocation(TestingCore.MODID, "render/model.obj"));

}
