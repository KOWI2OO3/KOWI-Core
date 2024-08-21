package kowi2003.core.common.blocks.functions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A functional interface for creating block entities.
 * @param <T> The type of block entity to create.
 * 
 * @author KOWI2003
 */
public interface IBlockEntityProvider<T extends BlockEntity> {
    
    /**
     * Creates a new block entity.
     * @param position The position of the block entity.
     * @param state The state of the block entity.
     * @return The new block entity.
     */
    T newBlockEntity(BlockPos position, BlockState state);
}
