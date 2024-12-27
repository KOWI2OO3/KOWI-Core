package kowi2003.core.common.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import kowi2003.core.common.helpers.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ConnectorBlock extends DefaultBlock {
	public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
	public static final BooleanProperty EAST = BlockStateProperties.EAST;
	public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
	public static final BooleanProperty WEST = BlockStateProperties.WEST;
	public static final BooleanProperty UP = BlockStateProperties.UP;
	public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

	public IBlockConnector blockConnector;

	/**
     * Creates a new horizontal block
     * @param properties the properties of the block
     * @param sound the sound type the block should make
     * @param shape the shape used for collision and clipping
     */
    public ConnectorBlock(Properties properties, SoundType sound, VoxelShape shape, IBlockConnector blockConnector) {
        super(properties, sound, shape);
        this.blockConnector = blockConnector;
    }

	/**
     * Creates a new horizontal block
     * @param properties the properties of the block
     * @param sound the sound type the block should make
     * @param shape the shape used for collision and clipping
     */
    public ConnectorBlock(Properties properties, SoundType sound, Supplier<VoxelShape> shape, IBlockConnector blockConnector) {
        super(properties, sound, shape);
        this.blockConnector = blockConnector;
    }

	/**
     * Creates a new horizontal block
     * @param properties the properties of the block
     * @param shape the shape used for collision and clipping
     */
	public ConnectorBlock(Properties properties, VoxelShape shape, IBlockConnector blockConnector) {
        super(properties, shape);
        this.blockConnector = blockConnector;
    }

	/**
     * Creates a new horizontal block
     * @param properties the properties of the block
     * @param shape the shape used for collision and clipping
     */
    public ConnectorBlock(Properties properties, Supplier<VoxelShape> shape, IBlockConnector blockConnector) {
        super(properties, shape);
        this.blockConnector = blockConnector;
    }

    /**
     * Creates a new block with blockentity
     * @param properties the properties of the block
     * @param sound the sound type the block should make
     */
	public ConnectorBlock(Properties properties, SoundType sound, IBlockConnector blockConnector) {
        super(properties, sound);
        this.blockConnector = blockConnector;
    }

	/**
     * Creates a new horizontal block
     * @param properties the properties of the block
     */
    public ConnectorBlock(Properties properties, IBlockConnector blockConnector) {
        super(properties);
        this.blockConnector = blockConnector;
    }
    
	@Override
	protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}
	
	protected void fillStateContainer(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}
	
	@Override
	public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
		return getBlockState(context.getLevel(), super.getStateForPlacement(context), context.getClickedPos());
	}
	
	@Override
	public boolean isPathfindable(@Nonnull BlockState p_53306_, @Nonnull BlockGetter p_53307_, @Nonnull BlockPos p_53308_, @Nonnull PathComputationType p_53309_) {
		return false;
	}
	
	@Override
	public void neighborChanged(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Block block,
        @Nonnull BlockPos otherPos, boolean p_60514_) {
		state = getBlockState(level, state, pos);
		BlockEntity tile = level.getBlockEntity(pos);
		level.setBlockAndUpdate(pos, state);
		if(tile != null)
			level.setBlockEntity(tile);
	}
	
	public BlockState getBlockState(Level level, BlockState state, BlockPos pos) {
		return state.setValue(NORTH, connectsTo(level, pos, Direction.NORTH)).setValue(EAST, connectsTo(level, pos, Direction.EAST))
				.setValue(SOUTH, connectsTo(level, pos, Direction.SOUTH)).setValue(WEST, connectsTo(level, pos, Direction.WEST))
				.setValue(DOWN, connectsTo(level, pos, Direction.DOWN)).setValue(UP, connectsTo(level, pos, Direction.UP));
	}
	
	public boolean connectsTo(Level level, BlockPos pos, Direction dir) {
		pos = Utils.offset(pos, dir, 1);
		BlockState state = level.getBlockState(pos);
		return blockConnector.connectsTo(level, state, pos, dir);
	}
	
	protected VoxelShape getBaseShape() {
		return Shapes.box(0.356, 0.356, 0.356, 0.644, 0.644, 0.644);
	}
	
	protected VoxelShape getNorthShape() {
		return Shapes.box(0.356, 0.362, 0, 0.642, 0.643, 0.4);
	}
	
	protected VoxelShape getEastShape() {
		return Shapes.box(0.6, 0.363, 0.356, 1, 0.643, 0.642);
	}
	
	protected VoxelShape getSouthShape() {
		return Shapes.box(0.357, 0.362, 0.6, 0.644, 0.643, 1);
	}
	
	protected VoxelShape getWestShape() {
		return Shapes.box(0, 0.362, 0.357, 0.4, 0.643, 0.644);
	}
	
	protected VoxelShape getUpShape() {
		return Shapes.box(0.357, 0.6, 0.356, 0.637, 1, 0.642);
	}
	
	protected VoxelShape getDownShape() {
		return Shapes.box(0.362, 0, 0.356, 0.643, 0.4, 0.642);
	}
	
	public VoxelShape getAssembledShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		 List<VoxelShape> shapes = new ArrayList<>();
		    shapes.add(getBaseShape());
		    if(state.getValue(NORTH)) shapes.add(getNorthShape());
		    if(state.getValue(EAST)) shapes.add(getEastShape());
		    if(state.getValue(SOUTH)) shapes.add(getSouthShape());
		    if(state.getValue(WEST)) shapes.add(getWestShape());
		    if(state.getValue(UP)) shapes.add(getUpShape());
		    if(state.getValue(DOWN)) shapes.add(getDownShape());
		    
		    VoxelShape result = Shapes.empty();
		    for(VoxelShape shape : shapes)
		        result = Shapes.join(result, shape, BooleanOp.OR);
		    
		    return result.optimize();
	}
	
	@Override
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
	    return getAssembledShape(state, world, pos, context);
	}
	
	@Override
	public RenderShape getRenderShape(@Nonnull BlockState p_49232_) {
		return RenderShape.MODEL;
	}
	
	public static interface IBlockConnector {
		boolean connectsTo(Level level, BlockState state, BlockPos pos, Direction dir);
	}
}
