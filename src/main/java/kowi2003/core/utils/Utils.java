package kowi2003.core.utils;

import java.util.Iterator;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A general utility methods class helping with lots of small tasks
 * 
 * @author KOWI2003 
 */
public class Utils {

  /**
   * Parses the given color array and ensures that the returned array is of length 4 (RGBA).
   *
   * @param color the color array to parse
   * @return a new color array of length 4 (RGBA)
   */
	public static float[] parseColor(float[] color) {
		return color == null ? new float[] {1f, 1f, 1f, 1f} : new float[] {
				color.length > 0 ? color[0] : 1.0f,
				color.length > 1 ? color[1] : 1.0f,
				color.length > 2 ? color[2] : 1.0f,
				color.length > 3 ? color[3] : 1.0f
		};
	}

    /**
     * gets a new block position relative to this position in the direction specified with the distance from the current position as specified
     * @param pos the position to use as origin
     * @param direction the direction of the offset
     * @param distance the distance of the offset
     * @return a new position relative to the origin position as specified
     */
    public static BlockPos offset(BlockPos pos, Direction direction, float distance) {
      if(direction == null)
        return pos;
      return pos.offset(
              (int) (direction.getStepX() * distance), 
              (int) (direction.getStepY() * distance), 
              (int) (direction.getStepZ() * distance));
    }

    /**
     * creates an iterable of block positions in the given area
     * @param min the minimum position
     * @param max the maximum position
     * @return an iterable of block positions in the given area
     */
    public static Iterable<BlockPos> getPositionsIn(Vec3 min, Vec3 max) {
      return getPositionsIn(
        new BlockPos((int) Math.floor(min.x), (int) Math.floor(min.y), (int) Math.floor(min.z)), 
        new BlockPos((int) Math.ceil(max.x), (int) Math.ceil(max.y), (int) Math.ceil(max.z)));
    }

    /**
     * creates an iterable of block positions in the given area
     * @param min the minimum position
     * @param max the maximum position
     * @return an iterable of block positions in the given area
     */
    public static Iterable<BlockPos> getPositionsIn(BlockPos min, BlockPos max) {
        var tmp = new BlockPos(min);
        final var minPosition = new BlockPos(Math.min(min.getX(), max.getX()), Math.min(min.getY(), max.getY()), Math.min(min.getZ(), max.getZ()));
        final var maxPosition = new BlockPos(Math.max(tmp.getX(), max.getX()), Math.max(tmp.getY(), max.getY()), Math.max(tmp.getZ(), max.getZ()));

        return () -> new Iterator<BlockPos>() {
          private BlockPos current = new BlockPos(minPosition);

          @Override
          public boolean hasNext() {
            return current.getZ() <= maxPosition.getZ();
          }

          @Override
          public BlockPos next() {
            var current = new BlockPos(this.current);
            this.current = new BlockPos(this.current.getX() + 1, this.current.getY(), this.current.getZ());
            if(this.current.getX() >= maxPosition.getX()) {
              this.current = new BlockPos(minPosition.getX(), this.current.getY() + 1, this.current.getZ());
              if(this.current.getY() >= maxPosition.getY()) {
                this.current = new BlockPos((int) minPosition.getX(), minPosition.getY(), this.current.getZ() + 1);
              }
            }

            return current;
          }
        };
    }

    /**
	 * checks whether an key is down
	 * @param key the key id as specified by GLFW <i><br>(keys: GLFW.GLFW_KEY_??</i>)
	 * @return
	 */
  @OnlyIn(Dist.CLIENT)
	public static boolean isKeyDown(int key) {
		return GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), key) == GLFW.GLFW_TRUE;
	}

}
