package kowi2003.core.contraptions;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

public class ContraptionWrapper implements BlockAndTintGetter {

    @Nonnull 
    private Level level;
    @Nonnull 
    private Contraption contraption;

    private Vector3f position;
    private Quaternionf rotation;

    @SuppressWarnings("null")
    public ContraptionWrapper(@Nonnull Contraption contraption, @Nonnull Level level) {
        this.contraption = contraption;
        this.position = new Vector3f();
        this.rotation = new Quaternionf();
        setLevel(level);
    }

    public void setLevel(@Nonnull Level level) {
        this.level = level;
    }

    @Nonnull public UUID id() { return contraption.id(); }

    @Nonnull public Level level() { return level; }
    @Nonnull public Contraption contraption() { return contraption; }
    @Nonnull public Vector3f position() { return position; }
    @Nonnull public Quaternionf rotation() { return rotation; }

    public void setPosition(@Nonnull Vector3f position) 
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
}
