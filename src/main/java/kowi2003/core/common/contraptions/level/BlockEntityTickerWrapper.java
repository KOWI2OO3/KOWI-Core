package kowi2003.core.common.contraptions.level;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

/**
 * A wrapper to tick a blockentity without needing to save the blockentity type.
 * @param <T> the type of blockentity
 * 
 * @author KOWI2003
 */
public record BlockEntityTickerWrapper<T extends BlockEntity>(@Nonnull Class<? extends BlockEntity> tileType, @Nonnull BlockEntityTicker<T> ticker) {
    
    /**
     * A wrapper to tick a blockentity without needing to save the blockentity type.
     * @param level the level the blockentity exists in
     * @param blockPos the position of the blockentity
     */
    @SuppressWarnings({ "unchecked" })
    public void tick(Level level, BlockPos blockPos) {
        var localTile = level.getBlockEntity(blockPos);
        if(localTile != null && tileType.isInstance(localTile))
            ticker.tick(level, blockPos, level.getBlockState(blockPos), (T)localTile);
    }

}
