package kowi2003.core.common.contraptions.level;

import kowi2003.core.common.contraptions.ContraptionWrapper;
import net.minecraft.world.level.Level;

public interface IVirtualLevel {
    
    void updateData(ContraptionWrapper wrapper, Level level);

}
