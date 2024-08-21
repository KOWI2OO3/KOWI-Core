package kowi2003.core.common.blocks;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import kowi2003.core.common.blockentities.functions.IInteractable;
import kowi2003.core.common.blocks.functions.IBlockEntityProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class ContainerBlock extends BaseEntityBlock {
    
    @Nullable
    IBlockEntityProvider<?> provider;

    SoundType sound;
	VoxelShape shape; 

	/**
     * Creates a new block with blockentity
     * @param properties the properties of the block
     * @param provider the provider of the blockentity
     */
    public ContainerBlock(Properties builder, IBlockEntityProvider<?> provider) {
		super(builder);
		this.provider = provider;
	}
	
	/**
     * Creates a new block with blockentity
     * @param properties the properties of the block
     * @param provider the provider of the blockentity
     * @param sound the sound type the block should make
     */
	public ContainerBlock(Properties builder, IBlockEntityProvider<?> provider, SoundType sound) {
		super(builder);
		this.provider = provider;
		this.sound = sound;
	}

    /**
     * Creates a new block with blockentity
     * @param properties the properties of the block
     * @param provider the provider of the blockentity
     * @param shape the shape used for collision and clipping
     */
	public ContainerBlock(Properties builder, IBlockEntityProvider<?> provider, VoxelShape shape) {
		super(builder);
	}

    /**
     * Creates a new block with blockentity
     * @param properties the properties of the block
     * @param provider the provider of the blockentity
     * @param sound the sound type the block should make
     * @param shape the shape used for collision and clipping
     */
	public ContainerBlock(Properties builder, IBlockEntityProvider<?> provider, SoundType sound, VoxelShape shape) {
		super(builder);
		this.provider = provider;
		this.sound = sound;
		this.shape = shape;
	}
    
	protected ContainerBlock(Properties builder) {
		this(builder, null);
	}
	
	public RenderShape getRenderShape(@Nullable BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Override
	public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
		return sound == null ? SoundType.STONE : sound;
	}
	
    @Override
	@SuppressWarnings("deprecation")
	public InteractionResult use(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull Player player,
			@Nonnull InteractionHand hand, @Nonnull BlockHitResult raytraceResult) {
		if(!world.isClientSide) {
			var tile = world.getBlockEntity(pos);
			if(tile instanceof IInteractable interactable)
				return interactable.use(state, world, pos, player, hand, raytraceResult);
			
            if(tile instanceof MenuProvider provider) {
                NetworkHooks.openScreen((ServerPlayer)player, provider, pos);
                return InteractionResult.SUCCESS;
            }
		}
		var tile = world.getBlockEntity(pos);
		return tile instanceof MenuProvider ? InteractionResult.SUCCESS : super.use(state, world, pos, player, hand, raytraceResult);
	}
	
	@Override
    @SuppressWarnings("deprecation")
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
		return this.shape == null ? super.getShape(state, world, pos, context) : this.shape;
	}
	
    @Override
	@SuppressWarnings("deprecation")
	public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter world,@Nonnull BlockPos pos, @Nonnull CollisionContext context) {
		return super.getCollisionShape(state, world, pos, context);
	}
	
    @Override
	@SuppressWarnings("deprecation")
	public VoxelShape getVisualShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
		return super.getVisualShape(state, world, pos, context);
	}
	
	@Override
	public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
		return provider == null ? null : provider.newBlockEntity(pos, state);
	}
	
	@Override
	public void playerWillDestroy(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Player player) {
		super.playerWillDestroy(world, pos, state, player);
	}
	
	@Override
	public List<ItemStack> getDrops(@Nonnull BlockState state, @Nonnull Builder lootBuilder) {
		List<ItemStack> stacks = new ArrayList<>();
		stacks.add(new ItemStack(this));
		return stacks;
	}
	
	@Override
	public void playerDestroy(@Nonnull Level world, @Nonnull Player player, @Nonnull BlockPos pos, @Nonnull BlockState state,
			@Nullable BlockEntity blockentity, @Nonnull ItemStack stack) {
		super.playerDestroy(world, player, pos, state, blockentity, stack);
	}

}
