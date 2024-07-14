package kowi2003.core.contraptions.level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joml.Quaterniond;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import kowi2003.core.contraptions.ContraptionHelper;
import kowi2003.core.contraptions.ContraptionWrapper;
import kowi2003.core.utils.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockEventData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.GameRules;
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
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.LevelTicks;
import net.minecraft.world.ticks.TickPriority;

/**
 * A virtual server level that is used to simulate contraptions in a server level.
 * 
 * @author KOWI2003
 */
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
    @Nonnull
    private final BiConsumer<ContraptionWrapper, BlockPos> onBlockUpdate;

    private boolean isHandlingTick = false;
    private final LevelTicks<Block> blockTicks = new LevelTicks<>(this::isPositionTicking, this.getProfilerSupplier());
    private final Map<ChunkPos, LevelChunkTicks<Block>> chunkTickers = new HashMap<>();
    
    private final ObjectLinkedOpenHashSet<BlockEventData> blockEvents = new ObjectLinkedOpenHashSet<>();
    private final List<BlockEventData> blockEventsToReschedule = new ArrayList<>(64);

    private final Map<BlockPos, BlockEntityTickerWrapper<?>> blockEntityTickers = new HashMap<>();

    private int randValue = RandomSource.create().nextInt();

    public VirtualSeverLevel(ContraptionWrapper wrapper, ServerLevel level, Consumer<ContraptionWrapper> onUpdate, BiConsumer<ContraptionWrapper, BlockPos> onBlockUpdate) {
        super(level.getServer(), (action) -> action.run(), createDummyAccess(level), (ServerLevelData) level.getLevelData(), level.dimension(), 
            level.registryAccess().registryOrThrow(Registries.LEVEL_STEM).get(LevelStem.OVERWORLD),
            constructDummyProgressListener(), level.isDebug(), 1, List.of(), false, new RandomSequences(1));

        this.wrapper = wrapper;
        this.level = level;
        this.onUpdate = onUpdate == null ? w -> {} : onUpdate;
        this.onBlockUpdate = onBlockUpdate == null ? (w, p) -> onUpdate.accept(wrapper) : onBlockUpdate;

        updateData(wrapper, level);
    }
  
    @Override
    public void tick(@Nonnull BooleanSupplier supplier) {
        if(isHandlingTick) return;
        isHandlingTick = true; 

        // Handling block ticks
        blockTicks.tick(getGameTime(), 65536, this::tickBlock);
        runBlockEvents();

        // Handling random ticks
        var randomTickSpeed = this.level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
        if(randomTickSpeed > 0) {
            for(var region : ContraptionHelper.getRegions(wrapper)) {
                for(int i = 0; i < randomTickSpeed; i++) {
                    // Updating the randValue normally done on the getBlockRandomPos method
                    randValue = randValue * 3 + 1013904223;
                    var blockpos = region.getRandomBlockPos(randValue);
                    var state = wrapper.getBlockState(blockpos);
                    if (state.isRandomlyTicking()) {
                        state.randomTick(this, blockpos, this.random);
                    }
                }
            }
        }

        tickBlockEntities();

        isHandlingTick = false;
    }

    private void tickBlock(@Nonnull BlockPos blockpos, @Nonnull Block block) {
        BlockState blockstate = this.getBlockState(blockpos);
        if (blockstate.is(block)) {
            var tile = getBlockEntity(blockpos);
            if(tile != null) tile.setLevel(this);

            blockstate.tick(this, blockpos, this.random);
        }
    }

    @Override public LevelTicks<Block> getBlockTicks() { return this.blockTicks; }
    public boolean isPositionTicking(long position) { return true; }

    @Override
    protected void tickBlockEntities() {
        for (var entry : blockEntityTickers.entrySet()) {
            var ticker = entry.getValue();
            if(ticker != null)
                ticker.tick(this, entry.getKey());
        }
    }

    @Override public void tickChunk(@Nonnull LevelChunk chunk, int p_8716_) {}
    @Override public void startTickingChunk(@Nonnull LevelChunk chunk) {}
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

    @Override public boolean isHandlingTick() { return isHandlingTick; }

    // We don't handle entities in the virtual levels
    @Override public boolean isPositionEntityTicking(@Nonnull BlockPos blockpos) { return false; }

    @Override public void scheduleTick(@Nonnull BlockPos blockpos, @Nonnull Block block, int timeout) {
        super.scheduleTick(blockpos, block, timeout);
    }
    @Override public void scheduleTick(@Nonnull BlockPos blockpos, @Nonnull Block block, int timeout, @Nonnull TickPriority priority) {
        super.scheduleTick(blockpos, block, timeout, priority);
    }

    //TODO: fix fluid ticks
    @Override public void scheduleTick(@Nonnull BlockPos blockpos, @Nonnull Fluid fluid, int p_186472_) {
        System.out.println("Scheduling tick?");
    }
    @Override public void scheduleTick(@Nonnull BlockPos blockpos, @Nonnull Fluid fluid, int p_186476_, @Nonnull TickPriority priority) {
        System.out.println("Scheduling tick?");
    }

    @Override public boolean shouldTickBlocksAt(long position) { return true; }
    @Override public boolean shouldTickDeath(@Nonnull Entity entity) { return false; }

    @Override public void setCurrentlyGenerating(@Nullable Supplier<String> p_186618_) {}
    @Override public void addWorldGenChunkEntities(@Nonnull Stream<Entity> p_143328_) {}

    public void updateData(ContraptionWrapper wrapper, Level level) {
        if(level instanceof ServerLevel serverLevel)
            this.level = serverLevel;
        this.wrapper = wrapper;

        for (var blockpos : wrapper) {
            addChunkTicker(new ChunkPos(blockpos));

            var tile = wrapper.getBlockEntity(blockpos);
            if(tile != null)
                updateBlockEntityTicker(tile);
        }
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
        Block block = state.getBlock();
        blockpos = blockpos.immutable(); // Forge - prevent mutable BlockPos leaks
        BlockState old = getBlockState(blockpos);

        // Still have no clue what this is, but it's required for updating
        var flag = (p_46607_ & 64) != 0;

        wrapper.contraption().setState(blockpos, state);
        boolean doesOldHaveTile = old.hasBlockEntity();
        if (!this.level.isClientSide)
            old.onRemove(this, blockpos, state, flag);
        else if ((!old.is(block) || !state.hasBlockEntity()) && doesOldHaveTile) 
            this.removeBlockEntity(blockpos);

        if (!this.level.isClientSide)
            state.onPlace(this, blockpos, old, flag);

        // Setting block entity
        if(state.hasBlockEntity() && block instanceof EntityBlock entityBlock) {
            var existing = this.getBlockEntity(blockpos);
            var blockentity = entityBlock.newBlockEntity(blockpos, state);

            if(blockentity != null) {
                if(existing != null) {
                    if(blockentity.getType() == existing.getType())
                        blockentity.load(existing.serializeNBT());

                    removeBlockEntity(blockpos);
                }

                // Actually setting blockentity in world
                this.setBlockEntity(blockentity);
                
                updateBlockEntityTicker(blockentity);
                // Register Game event listeners
                
                blockentity.onLoad();

            }else if(existing != null)
                removeBlockEntity(blockpos);
        }

        this.markAndNotifyBlock(blockpos, null, old, state, p_46607_, p_46608_);

        if(!state.isAir())
            addChunkTicker(new ChunkPos(blockpos));

        return true;
    }

    @Override
    public void setBlockEntity(@Nonnull BlockEntity tile) {
        tile.setLevel(this);
        tile.clearRemoved();
        wrapper.contraption().setBlockEntity(tile.getBlockPos(), tile);
        onBlockUpdate.accept(wrapper, tile.getBlockPos());
    }

    @Override
    public void blockEntityChanged(@Nonnull BlockPos blockpos) {
        var tile = wrapper.getBlockEntity(blockpos);
        if(tile == null) return;

        tile.setLevel(this);
        onBlockUpdate.accept(wrapper, blockpos);
    }

    @SuppressWarnings("unchecked")
    private <T extends BlockEntity> void updateBlockEntityTicker(T tile) {
        var state = tile.getBlockState();
        var ticker = state.getTicker(this, (BlockEntityType<T>)tile.getType());
        if(ticker != null) 
            blockEntityTickers.put(tile.getBlockPos(), new BlockEntityTickerWrapper<T>(tile.getClass(), ticker));
        else 
            blockEntityTickers.remove(tile.getBlockPos());
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
        // onUpdate.accept(wrapper);
    }

    @Override
    public void addFreshBlockEntities(@Nonnull Collection<BlockEntity> beList) {
        for(var tile : beList)
            setBlockEntity(tile);
    }

    @Override
    public boolean addFreshEntity(@Nonnull Entity entity) {
        var position = entity.position();
        var rotation = new Quaterniond(wrapper.rotation()).conjugate();
        position = MathHelper.rotateVector(position, rotation).add(wrapper.position());
        entity.moveTo(position.x(), position.y(), position.z());
        return level.addFreshEntity(entity);
    }

    @Override
    public void markAndNotifyBlock(@Nonnull BlockPos blockpos, @Nullable LevelChunk levelchunk, @Nonnull BlockState oldState,
            @Nonnull BlockState updatedState, int p_46607_, int p_46608_) {
        Block block = updatedState.getBlock();
        BlockState blockstate1 = getBlockState(blockpos);
        {
            {
                if (blockstate1 == updatedState) {
                    if (oldState != blockstate1) {
                        this.setBlocksDirty(blockpos, oldState, blockstate1);
                    }

                    if ((p_46607_ & 2) != 0 && (!this.isClientSide || (p_46607_ & 4) == 0) && this.isClientSide) {
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

                    this.onBlockStateChange(blockpos, oldState, blockstate1);
                    updatedState.onBlockStateChange(this, blockpos, oldState);
                }
            }
        }
    }

    @Override
    public void onBlockStateChange(@Nonnull BlockPos blockpos, @Nonnull BlockState oldState, @Nonnull BlockState newState) {
        super.onBlockStateChange(blockpos, oldState, newState);
        onBlockUpdate.accept(wrapper, blockpos);
    }

    @Override
    public void blockUpdated(@Nonnull BlockPos blockpos, @Nonnull Block block) {
        super.blockUpdated(blockpos, block);
        onBlockUpdate.accept(wrapper, blockpos);
    }

    @Override public RegistryAccess registryAccess() { return level == null ? REGISTRY_ACCESS : level.registryAccess(); }
    @Override public Holder<Biome> getUncachedNoiseBiome(int p_203775_, int p_203776_, int p_203777_) { return null; }

    @Override
    public void sendBlockUpdated(@Nonnull BlockPos blockpos, @Nonnull BlockState oldstate, @Nonnull BlockState state, int p_8758_) {
        onBlockUpdate.accept(wrapper, blockpos);
    }

    public void blockEvent(@Nonnull BlockPos blockpos, @Nonnull Block block, int p_8748_, int p_8749_) {
        this.blockEvents.add(new BlockEventData(blockpos, block, p_8748_, p_8749_));
    }

    private void runBlockEvents() {
        this.blockEventsToReschedule.clear();

        while(!this.blockEvents.isEmpty()) {
            BlockEventData blockeventdata = this.blockEvents.removeFirst();
            if (this.shouldTickBlocksAt(blockeventdata.pos())) {
                if (this.doBlockEvent(blockeventdata)) 
                    this.getServer().getPlayerList().broadcast((Player)null, (double)blockeventdata.pos().getX(), (double)blockeventdata.pos().getY(), (double)blockeventdata.pos().getZ(), 
                        64.0D, this.dimension(), new ClientboundBlockEventPacket(blockeventdata.pos(), blockeventdata.block(), blockeventdata.paramA(), blockeventdata.paramB()));
            } else 
                this.blockEventsToReschedule.add(blockeventdata);
        }

        this.blockEvents.addAll(this.blockEventsToReschedule);
    }

    private boolean doBlockEvent(BlockEventData p_8699_) {
        BlockState blockstate = this.getBlockState(p_8699_.pos());
        return blockstate.is(p_8699_.block()) ? blockstate.triggerEvent(this, p_8699_.pos(), p_8699_.paramA(), p_8699_.paramB()) : false;
    }

    @Override
    public void playSound(@Nullable Player player, double x, double y, double z,
            @Nonnull SoundEvent sound, @Nonnull SoundSource source, float volume, float pitch) {
        var position = new Vec3(x, y, z);
        var rotation = new Quaterniond(wrapper.rotation()).conjugate();
        position = MathHelper.rotateVector(position, rotation).add(wrapper.position());

        level.playSound(player, position.x, position.y, position.z, sound, source, volume, pitch);
    }

    @Override
    public <T extends ParticleOptions> int sendParticles(@Nonnull T p_8768_, double p_8769_, double p_8770_, double p_8771_, int p_8772_, double p_8773_, double p_8774_, double p_8775_, double p_8776_) {
      ClientboundLevelParticlesPacket clientboundlevelparticlespacket = new ClientboundLevelParticlesPacket(p_8768_, false, p_8769_, p_8770_, p_8771_, (float)p_8773_, (float)p_8774_, (float)p_8775_, (float)p_8776_, p_8772_);
      int i = 0;

      var players = this.level.getPlayers((p) -> true);
      for(int j = 0; j < players.size(); ++j) {
         ServerPlayer serverplayer = players.get(j);
         if (this.sendParticles(serverplayer, false, p_8769_, p_8770_, p_8771_, clientboundlevelparticlespacket)) {
            ++i;
         }
      }

      return i;
   }

    @Override
   public <T extends ParticleOptions> boolean sendParticles(@Nonnull ServerPlayer p_8625_, @Nonnull T p_8626_, boolean p_8627_, double p_8628_, double p_8629_, double p_8630_, int p_8631_, double p_8632_, double p_8633_, double p_8634_, double p_8635_) {
      Packet<?> packet = new ClientboundLevelParticlesPacket(p_8626_, p_8627_, p_8628_, p_8629_, p_8630_, (float)p_8632_, (float)p_8633_, (float)p_8634_, (float)p_8635_, p_8631_);
      return this.sendParticles(p_8625_, p_8627_, p_8628_, p_8629_, p_8630_, packet);
   }

   private boolean sendParticles(@Nonnull ServerPlayer p_8637_, boolean p_8638_, double x, double y, double z, Packet<?> p_8642_) {
      if (p_8637_.level() != this.level) {
         return false;
      } else {
        var position = new Vec3(x, y, z);
        var rotation = new Quaterniond(wrapper.rotation()).conjugate();
        position = MathHelper.rotateVector(position, rotation).add(wrapper.position());

         BlockPos blockpos = p_8637_.blockPosition();
         if (blockpos.closerToCenterThan(position, p_8638_ ? 512.0D : 32.0D)) {
            p_8637_.connection.send(p_8642_);
            return true;
         } else {
            return false;
         }
      }
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
    public boolean mayInteract(@Nonnull Player player, @Nonnull BlockPos blockpos) { return true; }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> Optional<T> getBlockEntity(@Nonnull BlockPos blockpos, @Nonnull BlockEntityType<T> type) {
        var tile = getBlockEntity(blockpos);
        return tile != null && tile.getType() == type ? Optional.of((T)tile) : Optional.empty();
    }

    @Override public int getHeight() { return wrapper.getHeight(); }
    @Override public int getMinBuildHeight() { return -64;  }
    @Override public int getMaxBuildHeight() { return 255; }

    @Override public float getShade(@Nonnull Direction direction, boolean p_8761_) { return wrapper.getShade(direction, p_8761_); }
    @Override public LevelLightEngine getLightEngine() { return wrapper.getLightEngine(); }
    @Override public int getBlockTint(@Nonnull BlockPos blockpos, @Nonnull ColorResolver colorResolver) { return wrapper.getBlockTint(blockpos, colorResolver); }

    @Override
    public List<Entity> getEntities(@Nullable Entity entity, @Nonnull AABB boundingBox, @Nonnull Predicate<? super Entity> filter) {
        return List.of();
    }

    public void update() { onUpdate.accept(wrapper); }

    private void addChunkTicker(ChunkPos chunkPos) {
        if(!chunkTickers.containsKey(chunkPos)) {
            var ticker = new LevelChunkTicks<Block>();
            chunkTickers.put(chunkPos, ticker);
            blockTicks.addContainer(chunkPos, ticker);
        }
    }

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
        } catch (Exception e) {}
        return null;
    }
    
}
