package kowi2003.core.common.blocks;

import java.util.Map;

import javax.annotation.Nonnull;

import kowi2003.core.common.blocks.functions.IBlockEntityProvider;
import kowi2003.core.common.helpers.ShapeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OrientableContainerBlock extends ContainerBlock {
    
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	protected Map<Boolean, Map<Direction, VoxelShape>> shapes;

	/**
     * Creates a new orientable block with blockentity
     * @param properties the properties of the block
     * @param provider the provider of the blockentity
     * @param sound the sound type the block should make
     * @param shape the shape used for collision and clipping
     */
    public OrientableContainerBlock(Properties properties, IBlockEntityProvider<?> provider, SoundType sound, VoxelShape shape) {
        super(properties, provider, sound);
		this.shapes = ShapeHelper.createOrientedShapes(shape);
    }

    /**
     * Creates a new orientable block with blockentity
     * @param properties the properties of the block
     * @param provider the provider of the blockentity
     * @param sound the sound type the block should make
     */
	public OrientableContainerBlock(Properties properties, IBlockEntityProvider<?> provider, SoundType sound) {
        super(properties, provider, sound);
    }

    /**
     * Creates a new orientable block with blockentity
     * @param properties the properties of the block
     * @param provider the provider of the blockentity
     * @param shape the shape used for collision and clipping
     */
	public OrientableContainerBlock(Properties properties, IBlockEntityProvider<?> provider, VoxelShape shape) {
        super(properties, provider);
		this.shapes = ShapeHelper.createOrientedShapes(shape);
    }

	/**
     * Creates a new orientable block with blockentity
     * @param properties the properties of the block
     */
	public OrientableContainerBlock(Properties builder, IBlockEntityProvider<?> provider) {
		super(builder, provider);
	}

	@Override
	protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
		builder.add(FACING);
        builder.add(UP);
	}
	
	@Override
	public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        var shouldBeUp = context.getClickedFace().getAxis() == Axis.Y ? context.getClickedFace() == Direction.DOWN : context.getClickLocation().y - context.getClickedPos().getY() > 0.5;
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(UP, shouldBeUp);
	}
	
	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos,
			Player player) {
		return super.getCloneItemStack(state, target, level, pos, player);
	}

	@Override
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos,
			@Nonnull CollisionContext context) {
		return shapes == null ? super.getShape(state, world, pos, context) : getShape(state.getValue(FACING), state.getValue(UP));
	}

    protected VoxelShape getShape(Direction direction, boolean isUp) 
    {
        if(!shapes.containsKey(isUp) || !shapes.get(isUp).containsKey(direction))
            return shapes.get(isUp).get(Direction.NORTH);

        return shapes.get(isUp).get(direction);
    }

}
