package io.kowi2003.testing_core.config;

import kowi2003.core.common.config.ConfigNotMapped;
import kowi2003.core.common.config.ConfigRange;
import kowi2003.core.common.config.ConfigValue;

public class CommonConfig {
    
    @ConfigRange(min = 0, max = 10)
    public int range = 0;

    @ConfigValue(displayName = "sType", description = "this description is just to test if it all works")
    public String stringType = "just a simple test with strings";

    // This value should be ignored by the mapping
    @ConfigNotMapped
    public float ignoredValue = 9;

}
