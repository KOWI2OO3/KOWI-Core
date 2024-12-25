package kowi2003.core.common.contraptions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public record Region(ContraptionWrapper wrapper, int x, int y, int z) {
    
    public static final int size = 16; 

    /**
     * Gets the bounding box of the region
     * @return the bounding box
     */
    public AABB boundingBox() {
        return new AABB(x * size , y * size, z * size, x * size + size-1, y * size + size-1, z * size + size-1);
    }

    /**
     * Clips the region with the given position, direction and distance
     * @param position the position to clip from (in world space)
     * @param direction the direction to clip in (in world space)
     * @param distance the distance to clip
     * @return true if the region is clipped, false otherwise
     */
    public boolean clip(Vec3 position, Vec3 direction, float distance) {
        var clipPoints = ContraptionHelper.getClipPoints(wrapper, position, direction, distance);
        return boundingBox().clip(clipPoints.getA(), clipPoints.getB()).isPresent();
    }

    /**
     * Gets a random block position within the region
     * @return the random block position
     */
    public BlockPos getRandomBlockPos(int randValue) {
        var min = getMinBlockPos();
        var sizeBits = 15;
        int i = randValue >> 2;
        return new BlockPos(min.getX() + (i & sizeBits), min.getY() + (i >> 16 & sizeBits), min.getZ() + (i >> 8 & sizeBits));
    }

    public BlockPos getMinBlockPos() {
        return new BlockPos(x * size, y * size, z * size);
    }

    public BlockPos getMaxBlockPos() {
        return new BlockPos(x * size + size - 1, y * size + size - 1, z * size + size - 1);
    }
}
