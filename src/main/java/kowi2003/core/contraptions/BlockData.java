package kowi2003.core.contraptions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Represents a block and its data
 * @param position the position of the block
 * @param state the state of the block
 * @param blockEntity the block entity of the block
 * @author KOWI2003
 */
public record BlockData(@Nonnull BlockPos position, @Nonnull BlockState state, @Nullable BlockEntity blockEntity) {

    /**
     * Serializes the block data into an NBT tag
     * @return the NBT tag
     */
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        tag.putLong("position", position.asLong());

        var stateTag = BlockState.CODEC.encodeStart(NbtOps.INSTANCE, state).result().orElse(null);
        if(stateTag == null)
            throw new IllegalStateException("Failed to serialize block state");
            
        tag.put("state", stateTag);

        // Saving the block entity into the entity tag
        if(blockEntity() != null) {
            var beTag = blockEntity().saveWithFullMetadata();
            tag.put("blockentity", beTag);
        }

        return tag;
    }

    /**
     * Deserializes the block data from the NBT tag
     * @param tag the tag to deserialize from
     * @return the block data
     */
    public static BlockData from(CompoundTag tag) {
         // Getting the block position from the key
         var position = BlockPos.of(tag.getLong("position"));

         // Getting the block state
         var state = BlockState.CODEC.parse(NbtOps.INSTANCE, tag.get("state")).result().orElse(null);
         if(state == null) return null;

        BlockEntity blockEntity = null;

         // Getting the block entity if it exists
         if(tag.contains("blockentity")) {
             var entityTag = tag.getCompound("blockentity");
             blockEntity = BlockEntity.loadStatic(position, state, entityTag);
         }
         
         return new BlockData(position, state, blockEntity);
    }
    
}
