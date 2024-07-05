package kowi2003.core.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * a simple class containg helper methods to make manipulation and usage of boundingboxes and voxel shapes easier
 * 
 * @author KOWI2003
 */
public class ShapeUtils {
    
    /**
	 * this methode rotates the aabb 180 degree around the y-axis
	 * @param AABB the aabb to rotate
	 * @return a new rotated aabb
	 */
    public static AABB oppositeAABB(AABB AABB) {
		double x1,x2,y1,y2,z1,z2;
		x1 = AABB.minX * -1d;
		x2 = AABB.maxX * -1d;
		z1 = AABB.minZ * -1d;
		z2 = AABB.maxZ * -1d;
		y1 = AABB.minY;
		y2 = AABB.maxY;
		return new AABB(x2, y1, z2, x1, y2, z1).move(1, 0, 1);
	}

    /**
	 * this methode rotates the aabb based on the facing
     * the assumption being that Direction.North is 0 degree rotation 
	 * @param AABB the aabb to rotate
	 * @return a new rotated aabb
	 */
    public static AABB rotateAABB(AABB AABB, Direction facing) {
		double  x1,x2,z1,z2;
		x1 = AABB.minX;
		x2 = AABB.maxX;
		z1 = AABB.minZ;
		z2 = AABB.maxZ;
		double[] bounds = fixRotation(facing, x1, z1, x2, z2);
        return new AABB(bounds[0], AABB.minY, bounds[1], bounds[2], AABB.maxY, bounds[3]);
	}

    /**
     * fixes the rotational problems for aabb rotation based on the facing
     * the assumption being that Direction.North is 0 degree rotation 
     * @param facing the facing to rotate to
     */
    private static double[] fixRotation(Direction facing, double var1, double var2, double var3, double var4)
    {
        switch(facing)
        {
            case WEST:
                double var_temp_1 = var1;
                var1 = 1.0F - var3;
                double var_temp_2 = var2;
                var2 = 1.0F - var4;
                var3 = 1.0F - var_temp_1;
                var4 = 1.0F - var_temp_2;
                break;
            case NORTH:
                double var_temp_3 = var1;
                var1 = var2;
                var2 = 1.0F - var3;
                var3 = var4;
                var4 = 1.0F - var_temp_3;
                break;
            case SOUTH:
                double var_temp_4 = var1;
                var1 = 1.0F - var4;
                double var_temp_5 = var2;
                var2 = var_temp_4;
                double var_temp_6 = var3;
                var3 = 1.0F - var_temp_5;
                var4 = var_temp_6;
                break;
            default:
                break;
        }
        return new double[]{var1, var2, var3, var4};
    }

    /**
     * rotates a list of aabb's based on the facing.
     * the assumption being that Direction.North is 0 degree rotation 
     * @param AABB a list of bounding boxes to rotate
     * @param facing the facing to rotate to, assuming the current aabb list is facing north
     * @return a new list of aabbs which are rotated based on the facing given
     */
    public static List<AABB> rotateAABB(List<AABB> AABB, Direction facing) {
		List<AABB> boxes = new ArrayList<AABB>();
		for(AABB box: AABB) {
			boxes.add(rotateAABB(box, facing));
		}
		return boxes;
	}

    /**
     * gets the optimized VoxelShape based on the list of bounding boxes
     * @param aabbs the list of boundingboxes to transform into the VoxelShape
     * @return the VoxelShape created from the list of bounding boxes
     */
    public static VoxelShape getShapeFromAABB(List<AABB> aabbs) {
		List<VoxelShape> shapes = new ArrayList<>();
		for(AABB aabb : aabbs)
            if(aabb != null) shapes.add(Shapes.create(aabb));
		
		VoxelShape result = Shapes.empty();
	    for(VoxelShape shape : shapes)
	        result = Shapes.joinUnoptimized(result, shape, BooleanOp.OR);
	    
	    return result.optimize();
	}

     /**
     * rotates a list of voxel shapes based on the facing.
     * the assumption being that Direction.North is 0 degree rotation 
     * @param shapes a list of the voxel shapes to rotate
     * @param facing the facing to rotate to, assuming the current voxel shape list is facing north
     * @return a new list of voxel shapes which are rotated based on the facing given
     */
    public static List<VoxelShape> rotateVoxelShape(List<VoxelShape> shapes, Direction facing) {
		List<AABB> aabbs = new ArrayList<>();
		shapes.forEach((shape) -> aabbs.addAll(shape.toAabbs()));
		List<AABB> newAabbs = rotateAABB(aabbs, facing);
		List<VoxelShape> newShapes = new ArrayList<>();
		newAabbs.forEach((aabb) -> {if(aabb != null) newShapes.add(Shapes.create(aabb));});
		return newShapes;
	}

    /**
     * rotates a voxel shape based on the facing.
     * the assumption being that Direction.North is 0 degree rotation 
     * @param shapes the voxel shape to rotate
     * @param facing the facing to rotate to, assuming the current voxel shape is facing north
     * @return a new voxel shape which is rotated based on the facing given
     */
    public static VoxelShape rotateVoxelShape(VoxelShape shape, Direction facing) {
		List<AABB> aabbs = shape.toAabbs();
		List<AABB> newAabbs = rotateAABB(aabbs, facing);
		return getShapeFromAABB(newAabbs);
	}


}
