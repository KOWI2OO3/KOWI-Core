package kowi2003.core.contraptions;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ContraptionHelper {
    
    /**
     * Translates the entire contraption by the given movement, note this moves the blocks in the contraption, not the contraption itself
     * it moves the blocks relative to the contraptions origin
     * @param contraption the contraption to move
     * @param offset the movement to apply
     */
    public void offset(Contraption contraption, BlockPos offset) {
        var blocks = new HashMap<BlockPos, BlockState>();
        var blockEntities = new HashMap<BlockPos, BlockEntity>();
        for (var position : contraption) {
            var state = contraption.getState(position);
            var blockEntity = contraption.getBlockEntity(position);
            blocks.put(position.offset(offset), state);
            blockEntities.put(position.offset(offset), blockEntity);
        }

        contraption.blocks.clear();
        contraption.blockEntities.clear();
        contraption.blocks.putAll(blocks);
        contraption.blockEntities.putAll(blockEntities);
    }

    /**
     * Gets all blocks in the contraption as BlockData
     * @param contraption the contraption to get the blocks from
     * @return a collection of BlockData
     */
    public Collection<BlockData> getBlocks(Contraption contraption) {
        return contraption.blocks.entrySet().stream()
            .map(entry -> new BlockData(entry.getKey(), entry.getValue(), contraption.getBlockEntity(entry.getKey())))
            .toList();
    }

    /**
     * Gets all blocks in the contraption as BlockData that match the given predicate
     * @param contraption the contraption to get the blocks from
     * @param predicate the predicate to filter the blocks
     * @return a collection of BlockData
     */
    public Collection<BlockPos> getPositions(Contraption contraption, Predicate<BlockData> predicate) {
        return getBlocks(contraption).stream()
            .filter(predicate)
            .map(BlockData::position)
            .toList();
    }

}
