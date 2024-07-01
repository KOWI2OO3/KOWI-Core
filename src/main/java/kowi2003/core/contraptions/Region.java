package kowi2003.core.contraptions;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public record Region(ContraptionWrapper wrapper, int x, int y, int z) {
    
    /**
     * Gets the bounding box of the region
     * @return the bounding box
     */
    public AABB boundingBox() {
        return new AABB(x * 8 , y * 8, z * 8, x * 8 + 7, y * 8 + 7, z * 8 + 7);
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

}
