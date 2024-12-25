package kowi2003.core.common.colliders;

import java.util.HashSet;
import java.util.Set;

import org.joml.Math;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * A helper class that provides methods to convert bounding boxes into sphere colliders
 * 
 * @author KOWI2003
 */
public final class CollisionHelper {
    
    /**
     * Converts the given bounding box into a set of sphere colliders that approximate the bounding box as closely as possible
     * @param boundingBox the bounding box to convert 
     * @return a set of sphere colliders that approximate the bounding box as closely as possible
     */
    public static Set<SphereCollider> convertToSpheres(AABB boundingBox) {
        var result = new HashSet<SphereCollider>();
        
        // Ignoring z for a second
        var differenceX = boundingBox.maxX - boundingBox.minX;
        var differenceY = boundingBox.maxY - boundingBox.minY;
        var differenceZ = boundingBox.maxZ - boundingBox.minZ;
        
        var smallest = Math.min(differenceX, Math.min(differenceY, differenceZ));
        var radius = smallest / 2;
        
        double epsilon = 0.0001;
        var spheresX = differenceX / radius - 1 - epsilon;
        var spheresY = differenceY / radius - 1 - epsilon;
        var spheresZ = differenceZ / radius - 1 - epsilon;
        
        for(int x = 0; x < spheresX; x++) {
            for(int y = 0; y < spheresY; y++) {
                for(int z = 0; z < spheresZ; z++) {
                    result.add(new SphereCollider(new Vec3(
                            boundingBox.minX + (x + 1) * radius, 
                            boundingBox.minY + (y + 1) * radius, 
                            boundingBox.minZ + (z + 1) * radius
                        ),
                        radius
                    ));
                }
            }
        }

        return result;
    }

}
