package kowi2003.core.contraptions;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joml.Quaternionf;

import kowi2003.core.contraptions.level.VirtualClientLevel;
import kowi2003.core.contraptions.level.VirtualSeverLevel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;

public class ContraptionWrapper implements BlockAndTintGetter, Iterable<BlockPos>, INBTSerializable<CompoundTag> {

    @Nonnull 
    private Level level;
    @Nonnull 
    private Contraption contraption;

    private Vec3 position;
    private Quaternionf rotation;

    @SuppressWarnings("null")
    public ContraptionWrapper(@Nonnull Contraption contraption, @Nonnull Level level) {
        this.contraption = contraption;
        this.position = new Vec3(0, 0, 0);
        this.rotation = new Quaternionf();
        setLevel(level);
    }

    public void setLevel(@Nonnull Level level) {
        this.level = level;
    }

    @Nonnull public UUID id() { return contraption.id(); }

    @Nonnull public Level internalLevel() { return level; }
    @Nonnull public Contraption contraption() { return contraption; }
    @Nonnull public Vec3 position() { return position; }
    @Nonnull public Quaternionf rotation() { return rotation; }

    public void setPosition(@Nonnull Vec3 position) 
    { 
        this.position = position; 
        // TODO: trigger update event
    } 
    public void setRotation(Quaternionf rotation) 
    { 
        this.rotation = rotation;
        // TODO: trigger update event (for example, to update the entities in the contraption)
     }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(@Nonnull BlockPos position) { return contraption.getBlockEntity(position);}

    @Override
    public BlockState getBlockState(@Nonnull BlockPos position) { 
        var state = contraption.getState(position);
        return state == null ? Blocks.AIR.defaultBlockState() : state;
    } 

    @Override
    public FluidState getFluidState(@Nonnull BlockPos position) { 
        var state = getBlockState(position);
        return state != null ? state.getFluidState() : null;
    } 

    @Nonnull 
    public Level contraptionLevel(Level level, Consumer<ContraptionWrapper> onUpdate, BiConsumer<ContraptionWrapper, BlockPos> onBlockUpdate) {
        if(contraption instanceof ILevelContainer container)
            return container.level();

        return level.isClientSide() ? 
            new VirtualClientLevel(this, (ClientLevel)level, onUpdate, onBlockUpdate) : 
            new VirtualSeverLevel(this, (ServerLevel)level, onUpdate, onBlockUpdate);
    }

    @Override
    public int getHeight() { return contraption.height(); }

    @Override
    public int getMinBuildHeight() { return -Integer.MAX_VALUE; }

    @Override
    public float getShade(@Nonnull Direction direction, boolean p_45523_) {
        return level.getShade(direction, p_45523_);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return level.getLightEngine();
    }

    @Override
    public int getBlockTint(@Nonnull BlockPos blockpos, @Nonnull ColorResolver resolver) {
        var position = ContraptionHelper.transposePoint(new Vec3(blockpos.getX(),blockpos.getY(),blockpos.getZ()), this);
        var levelPosition = new BlockPos((int)position.x(), (int)position.y(), (int)position.z());
        return level.getBlockTint(levelPosition, resolver);
    }
    
    /**
     * Gets the region at the given position in the contraption (in 16x16x16 blocks)
     * @param position the position to get the region at
     * @return the region at the given position
     */
    @Nonnull
    public Region getRegion(@Nonnull BlockPos position) {
        return new Region(this, position.getX()/Region.size, position.getY()/Region.size, position.getZ()/Region.size);
    }

    @Override
    public Iterator<BlockPos> iterator() {
        return contraption.iterator();
    }

    /**
     * Gets the block data at the given position
     * @param position the position to get the block data at
     * @return the block data at the given position
     */
    public BlockData getBlockData(BlockPos position) {
        return new BlockData(position, getBlockState(position), getBlockEntity(position));
    }

    /**
     * Gets all block entities of the given type in the contraption
     * @param <T> the type of block entity
     * @param type the type of block entity
     * @return all block entities of the given type in the contraption
     */
    public <T extends BlockEntity> Collection<T> getBlockEntities(Class<T> type) {
        return contraption().blockEntities.values().stream().filter(type::isInstance).map(type::cast).toList();
    }

    /**
     * Gets all block entities in the contraption
     * @return all block entities in the contraption
     */
    public Collection<BlockEntity> getBlockEntities() {
        return contraption().blockEntities.values();
    }

    @Override
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        tag.put("contraption", contraption().serializeNBT());

        var positionTag = new CompoundTag();
        positionTag.putDouble("x", position.x());
        positionTag.putDouble("y", position.y());
        positionTag.putDouble("z", position.z());
        tag.put("position", positionTag);

        var rotationTag = new CompoundTag();
        rotationTag.putFloat("x", rotation.x);
        rotationTag.putFloat("y", rotation.y);
        rotationTag.putFloat("z", rotation.z);
        rotationTag.putFloat("w", rotation.w);
        tag.put("rotation", rotationTag);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        contraption = Contraption.from(nbt.getCompound("contraption"));
        var positionTag = nbt.getCompound("position");
        position = new Vec3(positionTag.getDouble("x"), positionTag.getDouble("y"), positionTag.getDouble("z"));
        var rotationTag = nbt.getCompound("rotation");
        rotation = new Quaternionf(rotationTag.getFloat("x"), rotationTag.getFloat("y"), rotationTag.getFloat("z"), rotationTag.getFloat("w"));
    }

    /**
     * Creates a contraption wrapper from the given nbt tag
     * @param tag the tag to create the contraption wrapper from
     * @param level the level to create the contraption wrapper in
     * @return the contraption wrapper created from the given nbt tag
     */
    public static ContraptionWrapper from(CompoundTag tag, Level level) {
        var wrapper = new ContraptionWrapper(new Contraption(), level);
        wrapper.deserializeNBT(tag);
        return wrapper;
    }
}
