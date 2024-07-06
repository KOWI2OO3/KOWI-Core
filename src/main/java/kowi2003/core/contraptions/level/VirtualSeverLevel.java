package kowi2003.core.contraptions.level;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joml.Quaterniond;

import kowi2003.core.contraptions.ContraptionWrapper;
import kowi2003.core.utils.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.TickPriority;

public class VirtualSeverLevel extends ServerLevel implements IVirtualLevel {

    private static RegistryAccess REGISTRY_ACCESS = null;

    private static ServerLevel STATIC_LEVEL = null;

    private static LevelStorageAccess DUMMY_ACCESS = null;

    @Nonnull 
    private ContraptionWrapper wrapper;

    @Nonnull 
    private ServerLevel level;

    @Nonnull
    private final Consumer<ContraptionWrapper> onUpdate;

    public VirtualSeverLevel(ContraptionWrapper wrapper, ServerLevel level, Consumer<ContraptionWrapper> onUpdate) {
        super(level.getServer(), (action) -> action.run(), createDummyAccess(level), (ServerLevelData) level.getLevelData(), level.dimension(), 
            level.registryAccess().registryOrThrow(Registries.LEVEL_STEM).get(LevelStem.OVERWORLD),
            constructDummyProgressListener(), level.isDebug(), 1, List.of(), false, new RandomSequences(1));

        this.wrapper = wrapper;
        this.level = level;
        this.onUpdate = onUpdate == null ? w -> {} : onUpdate;
    }
  
    @Override
    public void tick(@Nonnull BooleanSupplier supplier) {
        //TODO tick contraption
    }

    @Override
    protected void tickBlockEntities() {
        // TODO: tick contraption block entities
    }

    @Override
    public void tickChunk(@Nonnull LevelChunk chunk, int p_8716_) {
        // TODO: figure out what this method is supposed to do
        // super.tickChunk(p_8715_, p_8716_);
    }

    @Override
    public void startTickingChunk(@Nonnull LevelChunk chunk) {
        // TODO: figure out what this method is supposed to do
        // super.startTickingChunk(p_184103_);
    }

    @Override public void tickCustomSpawners(boolean p_8800_, boolean p_8801_) {} // We don't have custom spawners

    @Override
    public void tickNonPassenger(@Nonnull Entity entity) {
        // TODO: figure out what this method is supposed to do
        // super.tickNonPassenger(p_8648_);
    }

    @Override protected void tickTime() { } // Propably not required as we don't have time

    @Override public float getTimeOfDay(float time) { return level == null ? 0 : level.getTimeOfDay(time); }
    @Override public long getDayTime() { return level.getDayTime(); }
    @Override public long getGameTime() { return level.getGameTime(); }

    @Override
    public boolean isHandlingTick() { return super.isHandlingTick(); } // TODO: make sure this is correct, assume it is a boolean for when it is in the middle of a tick

    // We don't handle entities in the virtual levels
    @Override public boolean isPositionEntityTicking(@Nonnull BlockPos blockpos) { return false; }

    // TODO: figure out what these methods are supposed to do
    @Override public void scheduleTick(@Nonnull BlockPos blockpos, @Nonnull Block block, int p_186472_) {}
	@Override public void scheduleTick(@Nonnull BlockPos blockpos, @Nonnull Block p_186466_, int p_186467_, @Nonnull TickPriority priority) {}
	@Override public void scheduleTick(@Nonnull BlockPos blockpos, @Nonnull Fluid fluid, int p_186472_) {}
	@Override public void scheduleTick(@Nonnull BlockPos blockpos, @Nonnull Fluid fluid, int p_186476_, @Nonnull TickPriority priority) {}

    @Override
    public boolean shouldTickBlocksAt(long position) {
        return false; //TODO: change such that it depends on block entities?
    }

    @Override public boolean shouldTickDeath(@Nonnull Entity entity) { return false; }

    @Override public void setCurrentlyGenerating(@Nullable Supplier<String> p_186618_) {}
    @Override public void addWorldGenChunkEntities(@Nonnull Stream<Entity> p_143328_) {}

    public void updateData(ContraptionWrapper wrapper, Level level) {
        if(level instanceof ServerLevel serverLevel)
            this.level = serverLevel;
        this.wrapper = wrapper;

        for (var tile : wrapper.getBlockEntities()) { tile.setLevel(this); }
    }

    @Override public ServerChunkCache getChunkSource() { return level == null ? STATIC_LEVEL.getChunkSource() : level.getChunkSource(); }

    @Override
    public void levelEvent(@Nullable Player player, int p_8685_, @Nonnull BlockPos blockpos, int p_8687_) {
        var position = new Vec3(blockpos.getX(), blockpos.getY(), blockpos.getZ());
        var rotation = new Quaterniond(wrapper.rotation()).conjugate();
        position = MathHelper.rotateVector(position, rotation).add(wrapper.position());
        blockpos = new BlockPos((int)position.x(), (int)position.y(), (int)position.z());
        level.levelEvent(player, p_8685_, blockpos, p_8687_);
    }

    @Override
    public void gameEvent(@Nullable Entity entity, @Nonnull GameEvent event, @Nonnull BlockPos blockpos) {
        level.gameEvent(entity, event, blockpos);
    }

    @Override
    public boolean setBlock(@Nonnull BlockPos blockpos, @Nonnull BlockState state, int p_46607_, int p_46608_) {
        var oldState = getBlockState(blockpos);

        var block = state.getBlock();
        
        if(oldState.hasBlockEntity()) {
            if(!level.isClientSide())
                oldState.onRemove(this, blockpos, oldState, false);
            if(!oldState.is(block) || !state.hasBlockEntity()) 
                removeBlockEntity(blockpos);
        }

        BlockEntity tile = null;
        if(state.hasBlockEntity()) {
            if(block instanceof EntityBlock entityBlock)
                tile = entityBlock.newBlockEntity(blockpos, state);

            if(tile != null) {
                tile.setLevel(this);
                tile.clearRemoved();
            }
        }

        wrapper.contraption().setBlock(blockpos, state, tile);
        blockEntityChanged(blockpos);
        onUpdate.accept(wrapper);

        markAndNotifyBlock(blockpos, null, oldState, state, p_46607_, p_46608_);

        return true;
    }

    @Override
    public void setBlockEntity(@Nonnull BlockEntity tile) {
        tile.setLevel(this);
        tile.clearRemoved();
        wrapper.contraption().setBlockEntity(tile.getBlockPos(), tile);
        onUpdate.accept(wrapper);
    }

    @Override
    public void blockEntityChanged(@Nonnull BlockPos blockpos) {
        var tile = wrapper.getBlockEntity(blockpos);
        if(tile == null) return;

        tile.setLevel(this);
        // TODO: use specific single/set of blocks update method
        onUpdate.accept(wrapper);
    }

    @Override
    public void save(@Nullable ProgressListener progressListener, boolean p_8645_, boolean p_8646_) {
        onUpdate.accept(wrapper);
    }

    @Override public BlockState getBlockState(@Nonnull BlockPos blockpos) { return wrapper.getBlockState(blockpos); }
    @Override public FluidState getFluidState(@Nonnull BlockPos blockpos) { return wrapper.getFluidState(blockpos); }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(@Nonnull BlockPos blockpos) { return wrapper.getBlockEntity(blockpos); }
    @Override public @Nullable BlockEntity getExistingBlockEntity(BlockPos blockpos) { return getBlockEntity(blockpos); }
    @Override public void removeBlockEntity(@Nonnull BlockPos blockpos) { wrapper.contraption().removeBlockEntity(blockpos); }

    @Override
    public void addBlockEntityTicker(@Nonnull TickingBlockEntity ticker) {
        // TODO: add tickers
        onUpdate.accept(wrapper);
    }

    @Override
    public void addFreshBlockEntities(@Nonnull Collection<BlockEntity> beList) {
        for(var tile : beList)
            setBlockEntity(tile);

        onUpdate.accept(wrapper);
    }

    @Override
    public void markAndNotifyBlock(@Nonnull BlockPos blockpos, @Nullable LevelChunk levelchunk, @Nonnull BlockState oldState,
        @Nonnull BlockState updatedState, int p_46607_, int p_46608_) {
        var block = updatedState.getBlock();
        var state = getBlockState(blockpos);

        if(state == updatedState) {
            if(oldState != updatedState)
                setBlocksDirty(blockpos, oldState, updatedState);

            if ((p_46607_ & 2) != 0 && (!this.isClientSide || (p_46607_ & 4) == 0)) {
                this.sendBlockUpdated(blockpos, oldState, updatedState, p_46607_);
            }

            if ((p_46607_ & 1) != 0) {
                this.blockUpdated(blockpos, oldState.getBlock());
                if (!this.isClientSide && updatedState.hasAnalogOutputSignal()) {
                    this.updateNeighbourForOutputSignal(blockpos, block);
                }
            }

            if ((p_46607_ & 16) == 0 && p_46608_ > 0) {
                int i = p_46607_ & -34;
                oldState.updateIndirectNeighbourShapes(this, blockpos, i, p_46608_ - 1);
                updatedState.updateNeighbourShapes(this, blockpos, i, p_46608_ - 1);
                updatedState.updateIndirectNeighbourShapes(this, blockpos, i, p_46608_ - 1);
            }

            this.onBlockStateChange(blockpos, oldState, state);
        }
    }

    @Override public RegistryAccess registryAccess() { return level == null ? REGISTRY_ACCESS : level.registryAccess(); }
    @Override public Holder<Biome> getUncachedNoiseBiome(int p_203775_, int p_203776_, int p_203777_) { return null; }

    @Override
    public void sendBlockUpdated(@Nonnull BlockPos blockpos, @Nonnull BlockState oldstate, @Nonnull BlockState state, int p_8758_) {
        // TODO: use specific single/set of blocks update method
        onUpdate.accept(wrapper);
    }

    @Override
    public void playSound(@Nullable Player player, double x, double y, double z,
            @Nonnull SoundEvent sound, @Nonnull SoundSource source, float volume, float pitch) {
        var position = new Vec3(x, y, z);
        var rotation = new Quaterniond(wrapper.rotation()).conjugate();
        position = MathHelper.rotateVector(position, rotation).add(wrapper.position());

        level.playSound(player, position.x, position.y, position.z, sound, source, volume, pitch);
    }

    @Override public String gatherChunkSourceStats() { return null; }

    @Override
    @Nullable
    public Entity getEntity(int entityId) { return level.getEntity(entityId); }

    @Override
    @Nullable
    public MapItemSavedData getMapData(@Nonnull String mapIdentifier) {
        return level.getMapData(mapIdentifier);
    }

    @Override public void setMapData(@Nonnull String mapIdentifier, @Nonnull MapItemSavedData mapData) { level.setMapData(mapIdentifier, mapData); }
    @Override public int getFreeMapId() { return level.getFreeMapId(); }

    // TODO: allow block progress
    @Override public void destroyBlockProgress(int p_8612_, @Nonnull BlockPos blockpos, int p_8614_) {}

    @Override public ServerScoreboard getScoreboard() { return level.getScoreboard(); }
    @Override public RecipeManager getRecipeManager() { return level.getRecipeManager(); }

    // TODO: maybe only get entities inside the contraption
    @Override public LevelEntityGetter<Entity> getEntities() { return level.getEntities(); }

    @Override
    @SuppressWarnings({ "null", "unchecked" })
    public <T extends BlockEntity> Optional<T> getBlockEntity(@Nonnull BlockPos blockpos, @Nonnull BlockEntityType<T> type) {
        var tile = getBlockEntity(blockpos);
        return tile.getType() == type ? Optional.of((T)tile) : Optional.empty();
    }

    @Override public int getHeight() { return wrapper.getHeight(); }

    // @Override
    // public int getMinBuildHeight() {
	// 	int min = Integer.MAX_VALUE;
	// 	for (BlockPos pos : wrapper)
	// 		if(pos.getY() < min)
	// 			min = pos.getY();
	// 	return min;
    // }

    @Override
    public int getMinBuildHeight() {
        return -64; 
    }

    @Override
    public int getMaxBuildHeight() {
        return 255;
    }

    @Override public float getShade(@Nonnull Direction direction, boolean p_8761_) { return wrapper.getShade(direction, p_8761_); }
    @Override public LevelLightEngine getLightEngine() { return wrapper.getLightEngine(); }
    @Override public int getBlockTint(@Nonnull BlockPos blockpos, @Nonnull ColorResolver colorResolver) { return wrapper.getBlockTint(blockpos, colorResolver); }

    @Override
    public List<Entity> getEntities(@Nullable Entity entity, @Nonnull AABB boundingBox, @Nonnull Predicate<? super Entity> filter) {
        return List.of();
    }

    public void update() { onUpdate.accept(wrapper); }

    private static final ChunkProgressListener constructDummyProgressListener() {
        return new ChunkProgressListener() {
            public void updateSpawnPos(@Nonnull ChunkPos position) {}
            public void stop() {}
            public void start() {}
            public void onStatusChange(@Nonnull ChunkPos position, @Nullable ChunkStatus status) {}
        };
    }

    private static final LevelStorageAccess createDummyAccess(ServerLevel level) {
        REGISTRY_ACCESS = level.registryAccess();
        STATIC_LEVEL = level;
        try {
            return DUMMY_ACCESS != null ? DUMMY_ACCESS : (DUMMY_ACCESS = LevelStorageSource.createDefault(level.getServer().getServerDirectory().toPath()).createAccess(""));
        } catch (Exception e) {
        }
        return null;
    }
    
}