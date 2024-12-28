package kowi2003.core.common.helpers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Supplier;

import kowi2003.core.common.blocks.functions.IBlockConnector;
import net.minecraft.world.level.block.Block;

public final class BlockHelper {
    
    public static IBlockConnector connectToAll() {
        return (level, block, state, pos, dir) -> true;
    }

    public static IBlockConnector connectToHorizontal() {
        return (level, block, state, pos, dir) -> dir.getAxis().isHorizontal();
    }

    public static IBlockConnector connectToVertical() {
        return (level, block, state, pos, dir) -> dir.getAxis().isVertical();
    }

    public static IBlockConnector connectToSolidFace() {
        return (level, block, state, pos, dir) -> level.getBlockState(pos).isFaceSturdy(level, pos, dir.getOpposite());
    }

    @SuppressWarnings("unchecked")
    public static IBlockConnector connectToSolidFaceOr(Supplier<Block>... blocks) {
        var blockSet = new HashSet<Block>(Arrays.stream(blocks).map(Supplier::get).toList());
        return (level, block, state, pos, dir) -> blockSet.contains(level.getBlockState(pos).getBlock()) || level.getBlockState(pos).isFaceSturdy(level, pos, dir.getOpposite());
    }

    @SuppressWarnings("unchecked")
    public static IBlockConnector connectTo(Supplier<Block>... blocks) {
        var blockSet = new HashSet<Block>(Arrays.stream(blocks).map(Supplier::get).toList());
        return (level, block, state, pos, dir) -> blockSet.contains(level.getBlockState(pos).getBlock());
    }

    public static IBlockConnector connectSelf() {
        return (level, block, state, pos, dir) -> block == state.getBlock();
    }

    public static IBlockConnector connectToSolidFaceOrSelf() {
        return (level, block, state, pos, dir) -> block == state.getBlock() ||
            level.getBlockState(pos).isFaceSturdy(level, pos, dir.getOpposite());
    }

    @SuppressWarnings("unchecked")
    public static IBlockConnector connectToOrSelf(Supplier<Block>... blocks) {
        var blockSet = new HashSet<Block>(Arrays.stream(blocks).map(Supplier::get).toList());
        return (level, block, state, pos, dir) -> block == state.getBlock() ||
            blockSet.contains(level.getBlockState(pos).getBlock());
    }

    @SuppressWarnings("unchecked")
    public static IBlockConnector connectToSolidFaceOrSelfOr(Supplier<Block>... blocks) {
        var blockSet = new HashSet<Block>(Arrays.stream(blocks).map(Supplier::get).toList());
        return (level, block, state, pos, dir) -> blockSet.contains(level.getBlockState(pos).getBlock()) || block == state.getBlock() ||
            level.getBlockState(pos).isFaceSturdy(level, pos, dir.getOpposite());
    }

}
