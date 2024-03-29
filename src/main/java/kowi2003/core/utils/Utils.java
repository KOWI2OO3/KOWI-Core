package kowi2003.core.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/**
 * A general utility methods class helping with lots of small tasks
 * 
 * @author KOWI2003 
 */
public class Utils {

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

}
