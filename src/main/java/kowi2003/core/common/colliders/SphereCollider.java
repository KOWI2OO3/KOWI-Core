package kowi2003.core.common.colliders;

import net.minecraft.world.phys.Vec3;

/**
 * A simple sphere collider class that provides methods to check for intersections with other colliders
 * 
 * @author KOWI2003
 */
public record SphereCollider(Vec3 center, double radius) {
    
    /**
     * Checks if the given sphere intersects with this sphere
     * @param other the other sphere to check
     * @return true if the spheres intersect, false otherwise
     */
    public boolean intersects(SphereCollider other) {
        return center.distanceTo(other.center) < radius + other.radius;
    }
    
    /**
     * Checks if the given point is inside the sphere
     * @param point the point to check
     * @return true if the point is inside the sphere, false otherwise
     */
    public boolean contains(Vec3 point) {
        return center.distanceTo(point) < radius;
    }

}
