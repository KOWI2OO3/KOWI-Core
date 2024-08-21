package kowi2003.core.common.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A helper class for creating and rotating shapes
 * 
 * @author KOWI2003
 */
public final class ShapeHelper {
    
    /**
     * Rotates the boundingbox 180 degrees horizontally
     * @param boundingbox the bounding box to rotate
     * @return the rotated bounding box
     */
    public static AABB oppositeAABB(AABB boundingbox)
    {
        return new AABB(
            boundingbox.maxX * -1d,
            boundingbox.minY,
            boundingbox.maxZ * -1d,
            boundingbox.minX * -1d,
            boundingbox.maxY,
            boundingbox.minZ * -1d
        ).move(1, 0, 1);
    }
    
    /**
     * Rotates a list of bounding boxes 180 degrees horizontally
     * @param boundingbox a list of bounding boxes to rotate
     * @return the rotated boundingbox list
     */
    public static List<AABB> oppositeAABB(List<AABB> boundingboxes)
    {
        var result = new ArrayList<AABB>();
        for (var boundingbox : boundingboxes) 
            result.add(oppositeAABB(boundingbox));
        return result;
    }

    /**
     * Rotates a bounding box around its y-axis (at 0, 0) to face the given direction.
     * @param boundingbox the bounding box to rotate
     * @param facing the facing to rotate to assuming the given bb is facing north
     * @return the rotated boundingbox
     */
    public static AABB rotateAABB(AABB boundingbox, Direction facing) 
    {
        var bounds = fixRotation(facing, boundingbox.minX, boundingbox.minZ, boundingbox.maxX, boundingbox.maxZ);
        return new AABB(bounds[0], boundingbox.minY, bounds[1], bounds[2], boundingbox.maxY, bounds[3]);
    }

    /**
     * Rotates a list of bounding boxes around its y-axis (at 0, 0) to face the given direction.
     * @param boundingboxes the bounding boxes to rotate
     * @param facing the facing to rotate to assuming the given bb is facing north
     * @return the rotated boundingbox list
     */
    public static List<AABB> rotateAABB(List<AABB> boundingboxes, Direction facing)
    {
        var result = new ArrayList<AABB>();
        for (var boundingbox : boundingboxes) 
            result.add(rotateAABB(boundingbox, facing));
        return result;
    }

    /**
     * Fixes rotation inaccuracies that occur when rotating an axis aligned boundingbox
     * @param facing the direction to rotate to
     * @param minX the minimum X of the semi rotated bounding box  
     * @param minZ the minimum Z of the semi rotated bounding box  
     * @param maxX the maximum X of the semi rotated bounding box
     * @param maxZ the maximum Z of the semi rotated bounding box
     * @return the bounds [minX, minZ, maxX, maxZ] of the fixed rotated boundingbox
     */
    private static double[] fixRotation(Direction facing, double minX, double minZ, double maxX, double maxZ)
    {
        switch(facing)
        {
            case WEST:
                double var_temp_1 = minX;
                minX = 1.0F - maxX;
                double var_temp_2 = minZ;
                minZ = 1.0F - maxZ;
                maxX = 1.0F - var_temp_1;
                maxZ = 1.0F - var_temp_2;
                break;
            case NORTH:
                double var_temp_3 = minX;
                minX = minZ;
                minZ = 1.0F - maxX;
                maxX = maxZ;
                maxZ = 1.0F - var_temp_3;
                break;
            case SOUTH:
                double var_temp_4 = minX;
                minX = 1.0F - maxZ;
                double var_temp_5 = minZ;
                minZ = var_temp_4;
                double var_temp_6 = maxX;
                maxX = 1.0F - var_temp_5;
                maxZ = var_temp_6;
                break;
            default:
                break;
        }
        return new double[]{minX, minZ, maxX, maxZ};
    }

    /**
     * Creates a VoxelShape from a list of bounding boxes
     * @param boundingboxes the list of bounding boxes to create the shape from
     * @return the VoxelShape created from the list of bounding boxes
     */
    public static VoxelShape getShapeFromAABB(List<AABB> boundingboxes)
    {
        var shapes = new ArrayList<VoxelShape>();
        for (var boundingbox : boundingboxes) 
            shapes.add(Shapes.create(boundingbox));
        var result = Shapes.empty();
        for (var shape : shapes) 
            result = Shapes.joinUnoptimized(result, shape, BooleanOp.OR);
        return result.optimize();
    }

    /**
     * Rotates a VoxelShape around its y-axis (at 0, 0) to face the given direction.
     * @param shape the VoxelShape to rotate
     * @param facing the facing to rotate to assuming the given shape is facing north
     * @return the rotated VoxelShape
     */
    public static VoxelShape rotateVoxelShape(VoxelShape shape, Direction facing)
    {
        return getShapeFromAABB(rotateAABB(shape.toAabbs(), facing));
    }

    public static Map<Direction, VoxelShape> createRotatedShapes(VoxelShape shape)
    {
        var result = new HashMap<Direction, VoxelShape>();
        for (var facing : Direction.values()) 
        {
            if(facing.getAxis() != Direction.Axis.Y)
                result.put(facing, rotateVoxelShape(shape, facing));
        }
        return result;
    }
}
