package kowi2003.core.contraptions;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joml.Quaternionf;
import org.joml.Vector4d;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContraptionHelper {
    
    /**
     * Translates the entire contraption by the given movement, note this moves the blocks in the contraption, not the contraption itself
     * it moves the blocks relative to the contraptions origin
     * @param contraption the contraption to move
     * @param offset the movement to apply
     */
    public void offset(@Nonnull Contraption contraption, @Nonnull BlockPos offset) {
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
    public Collection<BlockData> getBlocks(@Nonnull Contraption contraption) {
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
    public Collection<BlockPos> getPositions(@Nonnull Contraption contraption, @Nonnull Predicate<BlockData> predicate) {
        return getBlocks(contraption).stream()
            .filter(predicate)
            .map(BlockData::position)
            .toList();
    }

    /**
     * Clips the contraption with the given position and direction
     * @param wrapper the contraption wrapper to clip
     * @param position the position to clip from (in world space)
     * @param direction the direction to clip to (in world space)
     * @param maxDistance the maximum distance to clip
     * @param predicate the predicate to filter the blocks
     * @return the block hit result
     */
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("null")
    public static BlockHitResult clip(@Nonnull ContraptionWrapper wrapper, @Nonnull Vec3 position, @Nonnull Vec3 direction, float maxDistance, @Nullable Predicate<BlockData> predicate) {
        var start = position.subtract(new Vec3(wrapper.position()));
        direction = direction.normalize().multiply(maxDistance, maxDistance, maxDistance);
        var end = start.add(direction);

        var pose = new PoseStack();
        var wrapperRotation = new Quaternionf(wrapper.rotation()).conjugate();
        pose.rotateAround(wrapperRotation, 0.5f, 0, 0.5f);

        var transposedStart = new Vector4d(start.x, start.y, start.z, 1);
        var transposedEnd = new Vector4d(end.x, end.y, end.z, 1);   
        transposedStart = transposedStart.mul(pose.last().pose());
        transposedEnd = transposedEnd.mul(pose.last().pose());

        start = new Vec3(transposedStart.x, transposedStart.y, transposedStart.z);
        end = new Vec3(transposedEnd.x, transposedEnd.y, transposedEnd.z);

        var results = new HashMap<Double, BlockHitResult>();

        var hasPredicate = predicate != null;

        for (BlockPos blockPos : wrapper.contraption()) {
            if(hasPredicate && !predicate.test(wrapper.getBlockData(blockPos))) continue;

            var state = wrapper.getBlockState(blockPos);
            var result = state.getShape(wrapper, blockPos).clip(start, end, blockPos);
            if(result == null || result.getType() != HitResult.Type.BLOCK) continue;
            results.put(result.getLocation().subtract(position).lengthSqr(), result);
        }

        var min = results.keySet().stream().min(Double::compare).orElse(-1d);
        return min != -1 ? results.get(min) : null;
    }

}
