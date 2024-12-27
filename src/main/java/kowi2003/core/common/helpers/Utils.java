package kowi2003.core.common.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public final class Utils {
    
	public static BlockPos offset(BlockPos pos, Direction facing, int distance) {
		return facing == null ? pos : pos.offset(facing.getStepX() * distance, facing.getStepY() * distance, facing.getStepZ() * distance);
	}
}

