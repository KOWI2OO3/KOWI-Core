package kowi2003.core.contraptions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Represents a block and its data
 * @param position the position of the block
 * @param state the state of the block
 * @param blockEntity the block entity of the block
 * @author KOWI2003
 */
public record BlockData(@Nonnull BlockPos position, @Nonnull BlockState state, @Nullable BlockEntity blockEntity) {
    
}
