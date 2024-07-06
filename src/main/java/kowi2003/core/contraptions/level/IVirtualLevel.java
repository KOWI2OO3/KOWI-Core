package kowi2003.core.contraptions.level;

import kowi2003.core.contraptions.ContraptionWrapper;
import net.minecraft.world.level.Level;

public interface IVirtualLevel {
    
    void updateData(ContraptionWrapper wrapper, Level level);

}
