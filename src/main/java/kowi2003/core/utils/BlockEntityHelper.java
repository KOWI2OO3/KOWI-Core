package kowi2003.core.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Some utility methods to help with block entity handling
 * 
 * @author KOWI2003
 */
public class BlockEntityHelper {
    
    /**
     * Syncs this block entity to the client by marking it for update
     * @param tile the block entity to sync to the client
     */
    public static void syncToClient(BlockEntity tile) {
        var level = tile.getLevel();
        if(level == null)
            return;
        
        var pos = tile.getBlockPos();
        var state = tile.getBlockState();
        
        if(pos == null || state == null) 
            return;
        
        markBlockForUpdate(level, pos);

        level.markAndNotifyBlock(pos, level.getChunkAt(pos), state, state, 4, 4);
    }

    /**
     * Marks the block in the specified level at the specified position to send updates to the client
     * @param level the level that the block is in
     * @param pos the position of the block
     */
    public static void markBlockForUpdate(Level level, BlockPos pos)
	{
        if(level == null || pos == null) 
            return;
            
        var state = level.getBlockState(pos);
        if(state != null)
		level.sendBlockUpdated(pos, state, state, 3);
	}

}
