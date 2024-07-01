package kowi2003.core.contraptions;

import java.util.Iterator;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joml.Quaternionf;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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

    @Nonnull public Level level() { return level; }
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
    public int getBlockTint(@Nonnull BlockPos position, @Nonnull ColorResolver resolver) {
        var levelPosition = new BlockPos((int)this.position.x() + position.getX(), (int)this.position.y() + position.getY(), (int)this.position.z() + position.getZ());
        return level.getBlockTint(levelPosition, resolver);
    }
    
    /**
     * Gets the region at the given position in the contraption (in 8x8x8 blocks)
     * @param position the position to get the region at
     * @return the region at the given position
     */
    @Nonnull
    public Region getRegion(@Nonnull BlockPos position) {
        return new Region(this, position.getX()/8, position.getY()/8, position.getZ()/8);
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
