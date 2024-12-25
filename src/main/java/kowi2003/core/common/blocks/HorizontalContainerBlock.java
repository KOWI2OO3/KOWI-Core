package kowi2003.core.common.blocks;

import java.util.Map;

import javax.annotation.Nonnull;

import kowi2003.core.common.blocks.functions.IBlockEntityProvider;
import kowi2003.core.common.helpers.ShapeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A rotatable block with a block entity, which can be rotated around the y-axis.
 * 
 * @author KOWI2003
 */
public class HorizontalContainerBlock extends ContainerBlock {
    
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	protected Map<Direction, VoxelShape> shapes;

    /**
     * Creates a new horizontal block with blockentity
     * @param properties the properties of the block
     * @param provider the provider of the blockentity
     * @param sound the sound type the block should make
     * @param shape the shape used for collision and clipping
     */
    public HorizontalContainerBlock(Properties properties, IBlockEntityProvider<?> provider, SoundType sound, VoxelShape shape) {
        super(properties, provider, sound);
        shapes = ShapeHelper.createHorizontalShapes(shape);
    }

    /**
     * Creates a new block with blockentity
     * @param properties the properties of the block
     * @param provider the provider of the blockentity
     * @param shape the shape used for collision and clipping
     */
	public HorizontalContainerBlock(Properties properties, IBlockEntityProvider<?> provider, VoxelShape shape) {
        super(properties, provider);
        shapes = ShapeHelper.createHorizontalShapes(shape);
    }

    /**
     * Creates a new block with blockentity
     * @param properties the properties of the block
     * @param provider the provider of the blockentity
     * @param sound the sound type the block should make
     */
	public HorizontalContainerBlock(Properties properties, IBlockEntityProvider<?> provider, SoundType sound) {
        super(properties, provider);
    }

    /**
     * Creates a new horizontal block with blockentity
     * @param properties the properties of the block
     * @param provider the provider of the blockentity
     */
	public HorizontalContainerBlock(Properties builder, IBlockEntityProvider<?> provider) {
		super(builder, provider);
	}

	@Override
	protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}
	
	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos,
			Player player) {
		return super.getCloneItemStack(state, target, level, pos, player);
	}

	@Override
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos,
			@Nonnull CollisionContext context) {
		return shapes == null ? super.getShape(state, world, pos, context) : shapes.get(state.getValue(FACING));
	}
}
