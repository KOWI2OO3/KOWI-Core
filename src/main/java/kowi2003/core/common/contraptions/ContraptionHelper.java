package kowi2003.core.common.contraptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector4d;

import com.mojang.blaze3d.vertex.PoseStack;

import kowi2003.core.common.helpers.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import oshi.util.tuples.Pair;

public class ContraptionHelper {
    
    /**
     * Translates the entire contraption by the given movement, note this moves the blocks in the contraption, not the contraption itself
     * it moves the blocks relative to the contraptions origin
     * @param contraption the contraption to move
     * @param offset the movement to apply
     */
    public static void offset(@Nonnull Contraption contraption, @Nonnull BlockPos offset) {
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
     * Translates the entire contraption by the given movement, note this moves the blocks in the contraption, not the contraption itself
     * it moves the blocks relative to the contraptions origin
     * @param contraption the contraption to move
     * @param offset the movement to apply
     */
    public static Contraption withOffset(@Nonnull Contraption contraption, @Nonnull BlockPos offset) {
        var blocks = new HashMap<BlockPos, BlockState>();
        var blockEntities = new HashMap<BlockPos, BlockEntity>();
        for (var position : contraption) {
            var state = contraption.getState(position);
            var blockEntity = contraption.getBlockEntity(position);
            blocks.put(position.offset(offset), state);
            blockEntities.put(position.offset(offset), blockEntity);
        }

        return new Contraption(blocks, blockEntities);
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
     * Gets the min and max block positions of the contraption
     * @param contraption the contraption to get the block positions from
     * @return a pair of the min and max block positions
     */
    public static Pair<BlockPos, BlockPos> getMinMaxBlockPos(@Nonnull Contraption contraption) {
        if(contraption.blocks.size() == 0) return new Pair<>(new BlockPos(0, 0, 0), new BlockPos(0, 0, 0));
        
        var min = new BlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        var max = new BlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (var position : contraption) {
            min = new BlockPos(Math.min(min.getX(), position.getX()), Math.min(min.getY(), position.getY()), Math.min(min.getZ(), position.getZ()));
            max = new BlockPos(Math.max(max.getX(), position.getX()), Math.max(max.getY(), position.getY()), Math.max(max.getZ(), position.getZ()));
        }

        return new Pair<>(min, max);
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
        var clipPoints = getClipPoints(wrapper, position, direction, maxDistance);
        var start = clipPoints.getA();
        var end = clipPoints.getB();

        // Storing results to later find the closest block
        var results = new HashMap<Double, BlockHitResult>();
        var hasPredicate = predicate != null;

        // var positions = Utils.getPositionsIn(start, end);
        var positions = wrapper.contraption();
        
        for (BlockPos blockPos : positions) {
            if(!wrapper.contraption().blocks.containsKey(blockPos)) continue;
            if(hasPredicate && !predicate.test(wrapper.getBlockData(blockPos))) continue;

            var state = wrapper.getBlockState(blockPos);
            var result = wrapper.clipWithInteractionOverride(start, end, blockPos, state.getShape(wrapper, blockPos), state);
            // var result = state.getShape(wrapper, blockPos).clip(start, end, blockPos);
            if(result == null || result.getType() != HitResult.Type.BLOCK) continue;
            results.put(result.getLocation().subtract(start).lengthSqr(), result);
        }

        var min = results.keySet().stream().min(Double::compare).orElse(-1d);
        return min != -1 ? results.get(min) : null;
    }

    /**
     * Gets the starting and ending clip points of the contraption with the given position and direction
     * @param wrapper the contraption wrapper to clip
     * @param position the position to clip from (in world space)
     * @param direction the direction to clip to (in world space)
     * @param maxDistance the maximum distance to clip
     * @return a pair of the start and end clip points
     */
    public static Pair<Vec3, Vec3> getClipPoints(@Nonnull ContraptionWrapper wrapper, @Nonnull Vec3 position, @Nonnull Vec3 direction, float maxDistance) {
        var start = position.subtract(wrapper.position());
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
        
        return new Pair<>(start, end);
    }

    /**
     * Gets all regions of the contraption
     * @param wrapper the contraption wrapper to get the regions from
     * @return an array of regions
     */
    public static Region[] getRegions(ContraptionWrapper wrapper) {
        var minMax = getMinMaxBlockPos(wrapper.contraption());
        var min = minMax.getA();
        var max = minMax.getB();
        var regions = new ArrayList<Region>();
        for(int x = min.getX(); x <= max.getX(); x += Region.size) {
            for(int y = min.getY(); y <= max.getY(); y += Region.size) {
                for(int z = min.getZ(); z <= max.getZ(); z += Region.size) {
                    regions.add(new Region(wrapper, x/Region.size, y/Region.size, z/Region.size));
                }
            }
        }
        return regions.toArray(Region[]::new);
    }

    /**
     * Transposes the given position from the contraption space to world space
     * @param position the position to transpose in contraption space
     * @param wrapper the contraption wrapper to transpose to
     * @return the transposed position into world space
     */
    public static Vec3 transposePoint(Vec3 position, ContraptionWrapper wrapper) {
        var rotation = new Quaterniond(wrapper.rotation());
        return MathHelper.rotateVector(position, rotation).add(wrapper.position());
    }

}
