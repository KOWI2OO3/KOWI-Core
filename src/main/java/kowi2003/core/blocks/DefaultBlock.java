package kowi2003.core.blocks;

import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DefaultBlock extends Block {

    SoundType sound;

    public DefaultBlock(Properties properties, SoundType sound) {
        this(properties);
        this.sound = sound;
    }

    public DefaultBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
		return sound == null ? super.getSoundType(state, level, pos, entity) : sound;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player,
        @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    @SuppressWarnings("deprecation")
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos,
			@Nonnull CollisionContext context) {
		return super.getShape(state, world, pos, context);
	}
	
	@Override
    @SuppressWarnings("deprecation")
	public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos,
			@Nonnull CollisionContext context) {
		return super.getCollisionShape(state, level, pos, context);
	}
	
	@Override
    @SuppressWarnings("deprecation")
	public VoxelShape getVisualShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos,
			@Nonnull CollisionContext context) {
		return super.getVisualShape(state, level, pos, context);
	}
	
	@Override
	public void playerWillDestroy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Player player) {
		super.playerWillDestroy(level, pos, state, player);
	}
	
	@Override
	public void playerDestroy(@Nonnull Level level, @Nonnull Player player, @Nonnull BlockPos pos, @Nonnull BlockState state,
			@Nullable BlockEntity blockentity, @Nonnull ItemStack stack) {
		super.playerDestroy(level, player, pos, state, blockentity, stack);
	}

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(@Nonnull BlockState state, @Nonnull Builder builder) {
		List<ItemStack> stacks = super.getDrops(state, builder);
		if(stacks.isEmpty()) {
			stacks = new ArrayList<>();
			stacks.add(new ItemStack(this));
		}
		return stacks;
    }
    
	@Override
	public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
		return super.canHarvestBlock(state, level, pos, player);
	}
}
