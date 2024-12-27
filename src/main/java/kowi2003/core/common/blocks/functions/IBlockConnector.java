package kowi2003.core.common.blocks.functions;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockConnector {
    
    /**
     * This defines wether the block that has this interface can connect to a block in the direction with the specified state
     * @param level the level of the blocks
     * @param state the state of the othe blcok
     * @param pos the position of the other block
     * @param direction the direction of the neighbor block compared to the current block
     * @return wether to connecto to the neighbor block or not
     */
    boolean connectsTo(@Nonnull LevelReader level, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull Direction direction);
}
