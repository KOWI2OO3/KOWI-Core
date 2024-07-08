package kowi2003.core.contraptions.level;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joml.Quaterniond;

import kowi2003.core.contraptions.ContraptionWrapper;
import kowi2003.core.utils.MathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A virtual level which wraps a contraption into a client level.
 * This allows for contraptions to be interacted with like its a real level.
 * 
 * @author KOWI2003
 */
@OnlyIn(Dist.CLIENT)
public class VirtualClientLevel extends ClientLevel implements IVirtualLevel {

    @Nonnull 
    private ContraptionWrapper wrapper;

    @Nonnull 
    private ClientLevel level;

    @Nonnull
    private final Consumer<ContraptionWrapper> onUpdate;
    
    @Nonnull
    private final BiConsumer<ContraptionWrapper, BlockPos> onBlockUpdate;

    /**
     * Creates a new virtual client level for the given contraption wrapper and client level.
     * @param wrapper the internal data used for interactions with the contraption
     * @param level the client level to wrap
     * @param onUpdate the consumer to call when the contraption is updated
     */
    @SuppressWarnings("resource")
    public VirtualClientLevel(@Nonnull ContraptionWrapper wrapper, @Nonnull ClientLevel level, @Nullable Consumer<ContraptionWrapper> onUpdate, @Nullable BiConsumer<ContraptionWrapper, BlockPos> onBlockUpdate) {
        super(constructDummyPacketListener(), level.getLevelData(), level.dimension(), level.dimensionTypeRegistration(), 1, 1, 
            level.getProfilerSupplier(), Minecraft.getInstance().levelRenderer, level.isDebug(), 1);
        this.level = level;
        this.wrapper = wrapper;
        this.onUpdate = onUpdate == null ? w -> {} : onUpdate;
        this.onBlockUpdate = onBlockUpdate == null ? (w, p) -> this.onUpdate.accept(wrapper) : onBlockUpdate;
    }

    /**
     * Creates a new virtual client level for the given contraption wrapper and client level.
     * @param wrapper the internal data used for interactions with the contraption
     * @param level the client level to wrap
     */
    public VirtualClientLevel(ContraptionWrapper wrapper, ClientLevel level) {
        this(wrapper, level, null, null);
    }
    
    @Override public ResourceKey<Level> dimension() { return level.dimension(); }
    @Override public LevelTickAccess<Block> getBlockTicks() { return level.getBlockTicks(); }
    @Override public LevelTickAccess<Fluid> getFluidTicks() { return level.getFluidTicks(); }
    @Override public ClientChunkCache getChunkSource() { return null; }

    @Override
    public void levelEvent(@Nullable Player player, int p_104655_, @Nonnull BlockPos blockpos, int p_104657_) {
		var position = new Vec3(blockpos.getX(), blockpos.getY(), blockpos.getZ());
        var rotation = new Quaterniond(wrapper.rotation()).conjugate();

        position = MathHelper.rotateVector(position, rotation).add(wrapper.position());
        blockpos = new BlockPos((int)position.x(), (int)position.y(), (int)position.z());
        level.levelEvent(player, p_104655_, blockpos, p_104657_);
    }

    @Override public void gameEvent(@Nullable Entity entity, @Nonnull GameEvent event, @Nonnull BlockPos blockpos) {}

    @Override
    public boolean setBlock(@Nonnull BlockPos blockpos, @Nonnull BlockState state, int p_233645_, int p_233646_) {
        var oldState = getBlockState(blockpos);

        var block = state.getBlock();
        
        // Removing old block entity
        if(oldState.hasBlockEntity()) {
            if(!level.isClientSide())
                oldState.onRemove(this, blockpos, oldState, false);
            if(!oldState.is(block) || !state.hasBlockEntity())
                removeBlockEntity(blockpos);
            // TODO: should you not always remove the old block entity? (code retrieved from 1.18)
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
        markAndNotifyBlock(blockpos, null, oldState, state, p_233645_, p_233646_);
        onBlockUpdate.accept(wrapper, blockpos);

        return true;
    }

    @Override
    public void setBlockEntity(@Nonnull BlockEntity tile) {
        tile.setLevel(this);
        tile.clearRemoved();
        wrapper.contraption().setBlockEntity(tile.getBlockPos(), tile);
    }

    @Override
    public void blockEntityChanged(@Nonnull BlockPos blockpos) {
        var tile = wrapper.getBlockEntity(blockpos);
        if(tile == null)
            return;

        tile.setLevel(this);
        onBlockUpdate.accept(wrapper, blockpos);
    }

    @Override public @org.jetbrains.annotations.Nullable BlockEntity getExistingBlockEntity(BlockPos pos) { return wrapper.getBlockEntity(pos); }

    @Override
    public void addBlockEntityTicker(@Nonnull TickingBlockEntity ticker) {
        // TODO: allow tickers
    }

    @Override
    public void addFreshBlockEntities(@Nonnull Collection<BlockEntity> beList) {
        for(var tile : beList)
            setBlockEntity(tile);
    }

    @Override
    public void removeBlockEntity(@Nonnull BlockPos tile) {
        wrapper.contraption().removeBlockEntity(tile);
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
        onBlockUpdate.accept(wrapper, blockpos);
    }

    @SuppressWarnings("null")
    @Override public RegistryAccess registryAccess() { return level == null ? Minecraft.getInstance().level.registryAccess() : level.registryAccess(); }
    @Override public List<AbstractClientPlayer> players() { return level.players(); } //TODO: change to players in contraption?
    @Override public Holder<Biome> getUncachedNoiseBiome(int x, int y, int z) { return level.getUncachedNoiseBiome(x, y, z); }
    
    @Override
    public void sendBlockUpdated(@Nonnull BlockPos blockPos, @Nonnull BlockState oldState, @Nonnull BlockState updatedState, int p_104688_) {
        for (var tile : wrapper.getBlockEntities()) { tile.setLevel(this); }
        onBlockUpdate.accept(wrapper, blockPos);
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
    public void playSound(@Nullable Player player, @Nonnull Entity entity, @Nonnull SoundEvent sound, @Nonnull SoundSource source,
            float volume, float pitch) {
        level.playSound(player, entity, sound, source, volume, pitch);
    }

    @Override public String gatherChunkSourceStats() { return null; }

    @Override
    @Nullable
    public Entity getEntity(int entityId) {
        return level.getEntity(entityId);
    }

    @Override
    @Nullable
    public MapItemSavedData getMapData(@Nonnull String mapIdentifier) {
        return level.getMapData(mapIdentifier);
    }

    @Override
    public void setMapData(@Nonnull String mapIdentifier, @Nonnull MapItemSavedData mapData) {
        level.setMapData(mapIdentifier, mapData);
    }

    @Override
    public void destroyBlockProgress(int p_104634_, @Nonnull BlockPos blockpos, int p_104636_) {}

    @Override public Scoreboard getScoreboard() { return level.getScoreboard(); }
    @Override public RecipeManager getRecipeManager() { return level.getRecipeManager(); }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(@Nonnull BlockPos blockpos) {
        return wrapper.getBlockEntity(blockpos);
    }

    @Override public BlockState getBlockState(@Nonnull BlockPos blockpos) { return wrapper.getBlockState(blockpos); }
    @Override public Stream<BlockState> getBlockStates(@Nonnull AABB boundingBox) { return wrapper.getBlockStates(boundingBox); }


    @Override
    public FluidState getFluidState(@Nonnull BlockPos blockpos) {
        return wrapper.getFluidState(blockpos);
    }

    @Override
    public int getHeight() {
        return wrapper == null ? Integer.MAX_VALUE : wrapper.getHeight();
    }

    @Override public int getMinBuildHeight() { return -64; }
    @Override public int getMaxBuildHeight() { return 255; }

    @Override public float getShade(@Nonnull Direction direction, boolean p_104704_) { return wrapper.getShade(direction, p_104704_); }
    @Override public LevelLightEngine getLightEngine() { return wrapper.getLightEngine(); }

    @Override
    public int getBlockTint(@Nonnull BlockPos blockpos, @Nonnull ColorResolver resolver) { return wrapper.getBlockTint(blockpos, resolver); }
    
    public ContraptionWrapper contraptionWrapper() { return wrapper; }
    public ClientLevel wrappedLevel() { return level; }
	public void update() { onUpdate.accept(wrapper); }

    private static final ClientPacketListener constructDummyPacketListener() { return Minecraft.getInstance().getConnection(); }

    @Override
    public void addParticle(@Nonnull ParticleOptions particle, double x, double y, double z,
            double p_104710_, double p_104711_, double p_104712_) {
        var position = new Vec3(x, y, z);
        var rotation = new Quaterniond(wrapper.rotation()).conjugate();
        position = MathHelper.rotateVector(position, rotation).add(wrapper.position());
        level.addParticle(particle, position.x, position.y, position.z, p_104710_, p_104711_, p_104712_);
    }

    @Override
    public void addParticle(@Nonnull ParticleOptions particle, boolean p_104715_, double x, double y,
            double z, double p_104719_, double p_104720_, double p_104721_) {
        var position = new Vec3(x, y, z);
        var rotation = new Quaterniond(wrapper.rotation()).conjugate();
        position = MathHelper.rotateVector(position, rotation).add(wrapper.position());
        level.addParticle(particle, p_104715_, position.x, position.y, position.z, p_104719_, p_104720_, p_104721_);
    }

    public void updateData(ContraptionWrapper wrapper, Level level) {
        if(level instanceof ClientLevel clientLevel)
            this.level = clientLevel;
        this.wrapper = wrapper;
    }

}
