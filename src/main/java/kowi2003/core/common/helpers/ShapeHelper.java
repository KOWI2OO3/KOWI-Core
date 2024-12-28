package kowi2003.core.common.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
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
     * Mirrors the boundingbox along the given axis
     * @param boundingbox the bounding box to mirror
     * @param axis the axis to mirror along
     * @return the mirrored bounding box
     */
    public static AABB mirrorAABB(AABB boundingbox, Axis axis)
    {
        return new AABB(
            axis == Axis.X ? Math.min(mirror(boundingbox.minX), mirror(boundingbox.maxX)) : boundingbox.minX,
            axis == Axis.Y ? Math.min(mirror(boundingbox.minY), mirror(boundingbox.maxY)) : boundingbox.minY,
            axis == Axis.Z ? Math.min(mirror(boundingbox.minZ), mirror(boundingbox.maxZ)) : boundingbox.minZ,
            axis == Axis.X ? Math.max(mirror(boundingbox.minX), mirror(boundingbox.maxX)) : boundingbox.maxX,
            axis == Axis.Y ? Math.max(mirror(boundingbox.minY), mirror(boundingbox.maxY)) : boundingbox.maxY,
            axis == Axis.Z ? Math.max(mirror(boundingbox.minZ), mirror(boundingbox.maxZ)) : boundingbox.maxZ
        );
    }

    private static double mirror(double value)
    {
        return value * -1 + 1;
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
     * Mirrors a list of bounding boxes along the given axis
     * @param boundingboxes the list of bounding boxes to mirror
     * @param axis the axis to mirror along
     * @return the mirrored boundingbox list
     */
    public static List<AABB> mirrorAABB(List<AABB> boundingboxes, Axis axis)
    {
        var result = new ArrayList<AABB>();
        for (var boundingbox : boundingboxes) 
            result.add(mirrorAABB(boundingbox, axis));
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
                var tmp1 = minX;
                minX = 1.0F - maxX;
                var tmp2 = minZ;
                minZ = 1.0F - maxZ;
                maxX = 1.0F - tmp1;
                maxZ = 1.0F - tmp2;
                break;
            case NORTH:
                var tmp3 = minX;
                minX = minZ;
                minZ = 1.0F - maxX;
                maxX = maxZ;
                maxZ = 1.0F - tmp3;
                break;
            case SOUTH:
                var tmp4 = minX;
                minX = 1.0F - maxZ;
                var tmp5 = minZ;
                minZ = tmp4;
                var tmp6 = maxX;
                maxX = 1.0F - tmp5;
                maxZ = tmp6;
                break;
            default:
                break;
        }
        return new double[]{minX, minZ, maxX, maxZ};
    }

    /**
     * Rotates a AABB from an up state to the north state, meaning it is rotated 90 around the x-axis
     * @param boundingbox the flat AABB to rotate
     * @return the rotated flat AABB
     */
    private static AABB rotateFlatAABB(AABB boundingbox)
    {
        var tmp3 = boundingbox.minX;
        var minX = boundingbox.minY;
        var minY = 1.0F - boundingbox.maxX;
        var maxX = boundingbox.maxY;
        var maxY = 1.0F - tmp3;
        return new AABB(
            minX,
            minY,
            boundingbox.minZ,
            maxX,
            maxY,
            boundingbox.maxZ
        );
    }

    /**
     * Rotates a list of AABBs from an up state to the north state, meaning it is rotated 90 around the x-axis
     * @param boundingbox the flat AABB list to rotate
     * @return the rotated flat AABB list
     */
    private static List<AABB> rotateFlatAABB(List<AABB> boundingbox)
    {
        var result = new ArrayList<AABB>();
        for (AABB box : boundingbox)
            result.add(rotateFlatAABB(box));
        return result;
    }

    /**
     * Rotates a VoxelShape from an up state to the north state, meaning it is rotated 90 around the x-axis
     * @param shape the VoxelShape to rotate
     * @return the rotated VoxelShape
     */
    private static VoxelShape rotateFlatVoxelShape(VoxelShape shape)
    {
        return getShapeFromAABB(rotateFlatAABB(shape.toAabbs()));
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

    /**
     * Mirrors a VoxelShape along the given axis
     * @param shape the VoxelShape to mirror
     * @param axis the axis to mirror along
     * @return the mirrored VoxelShape
     */
    public static VoxelShape mirrorVoxelShape(VoxelShape shape, Axis axis)
    {
        return getShapeFromAABB(mirrorAABB(shape.toAabbs(), axis));
    }

    /**
     * Creates a map of VoxelShapes for each horizontal direction
     * @param shape the VoxelShape to create the map from
     * @return the map of VoxelShapes for each horizontal direction
     */
    public static Map<Direction, VoxelShape> createHorizontalShapes(VoxelShape shape)
    {
        var result = new HashMap<Direction, VoxelShape>();
        for (var facing : Direction.values()) 
        {
            if(facing.getAxis() != Direction.Axis.Y)
                result.put(facing, rotateVoxelShape(shape, facing));
        }
        return result;
    }

    /**
     * Creates a map of VoxelShapes for the two vertical states, where facing up is false and facing down is true
     * @param shape the VoxelShape to create the map from
     * @return the map of VoxelShapes for each vertical direction
     */
    public static Map<Boolean, VoxelShape> createVerticalShapes(VoxelShape shape)
    {
        var result = new HashMap<Boolean, VoxelShape>();
        result.put(false, shape);
        result.put(true, mirrorVoxelShape(shape, Axis.Y));
        return result;
    }

    /**
     * Creates a map of VoxelShapes for each direction
     * @param shape the VoxelShape to create the map from, this should be the up facing shape
     * @return the map of VoxelShapes for each direction
     */
    public static Map<Direction, VoxelShape> createRotatedShapes(VoxelShape shape)
    {
        var horizontalShape = rotateFlatVoxelShape(shape);
        var result = createHorizontalShapes(horizontalShape);
        result.put(Direction.UP, shape);
        result.put(Direction.DOWN, mirrorVoxelShape(shape, Axis.Y));
        
        return result;
    }

    /**
     * Creates a map of VoxelShapes for each direction and vertical state
     * @param shape the VoxelShape to create the map from, this should be the up facing shape
     * @return the map of VoxelShapes for each direction and vertical state
     */
    public static Map<Boolean, Map<Direction, VoxelShape>> createOrientedShapes(VoxelShape shape)
    {
        var result = new HashMap<Boolean, Map<Direction, VoxelShape>>();
        
        result.put(false, createHorizontalShapes(shape));
        result.put(true, createHorizontalShapes(mirrorVoxelShape(shape, Axis.Y)));
        
        return result;
    }
}
