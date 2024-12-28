package io.kowi2003.testing_core.init;

import java.util.function.Supplier;

import io.kowi2003.testing_core.config.CommonConfig;
import kowi2003.core.common.config.ConfigRegistry;
import net.minecraftforge.fml.config.ModConfig.Type;

public class ModConfig {
    
    public static Supplier<CommonConfig> commonConfig;

    public static void register() {
        commonConfig = ConfigRegistry.registerConfig(new CommonConfig(), Type.COMMON);
    }
}
