package kowi2003.core.contraptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;

public class Contraption implements Iterable<BlockPos>, INBTSerializable<CompoundTag> { // implements IBlockContainer, IRenderable(Provider), IUpdateable, LevelAccessor, IBoundinBoxProvider, INbtSerializablable{

    private UUID id = UUID.randomUUID();
    
    final Map<BlockPos, BlockState> blocks = new HashMap<>();
    final Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();

    /**
     * Default constructor
     */
    public Contraption() {}

    public Contraption(BlockData... blocks) {
        for (BlockData block : blocks) {
            if(block.state() == null || block.state().isAir()) continue;
            
            this.blocks.put(block.position(), block.state());
            if(block.blockEntity() != null)
                this.blockEntities.put(block.position(), block.blockEntity());
        }
    }

    /**
     * Constructor with block data and block entities
     * @param blocks block data
     * @param blockEntities block entities
     */
    public Contraption(@Nonnull Map<BlockPos, BlockState> blocks, @Nullable Map<BlockPos, BlockEntity> blockEntities) {
        blocks.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isAir());
        this.blocks.putAll(blocks);
        
        if(blockEntities != null) {
            blockEntities.entrySet().removeIf(entry -> entry.getValue() == null || !this.blocks.containsKey(entry.getKey()));
            this.blockEntities.putAll(blockEntities);
        }
    }

    /**
     * The id of the contraption
     * @return contraption id
     */
    public UUID id() { return id; }

    /**
     * Sets block data at position
     * @param position block position
     * @param state block data
     * @param blockEntity block entity
     * @return true if block data was set
     */
    public void setBlock(@Nonnull BlockPos position, @Nonnull BlockState state, @Nullable BlockEntity blockEntity) {
        setState(position, state);
        if(blockEntity != null && blocks.containsKey(position))
            setBlockEntity(position, blockEntity);
    }

    /**
     * Sets block data at position
     * @param position block position
     * @param state block data
     */
    public void setState(@Nonnull BlockPos position, @Nonnull BlockState state) {
        if(state.isAir())
            removeBlock(position);
        else
            blocks.put(position, state);
    }

    /**
     * Sets block data at position
     * @param position block position
     * @param state block data
     */
    public void setBlockEntity(@Nonnull BlockPos position, @Nonnull BlockEntity blockEntity) {
        if(blocks.keySet().contains(position))
            blockEntities.put(position, blockEntity);
    }

    /**
     * Removes block data at position
     * @param position block position
     * @return true if block data was removed
     */
    public boolean removeBlock(@Nonnull BlockPos position) {
        removeBlockEntity(position);
        return blocks.remove(position) != null;
    }

    /**
     * Removes block entity at position
     * @param position block position
     * @return true if block entity was removed
     */
    public boolean removeBlockEntity(@Nonnull BlockPos position) {
        return blockEntities.remove(position) != null;
    }

    /**
     * Gets block data at position
     * @param position block position
     * @return block data or null if no block at position
     */
    public BlockState getState(BlockPos position) {
        return blocks.get(position);
    }

    /**
     * Gets block entity at position
     * @param position block position
     * @return block entity or null if no block entity at position
     */
    public BlockEntity getBlockEntity(BlockPos position) {
        return blockEntities.get(position);
    }

    /**
     * Gets the id of the contraption
     * @return contraption id
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the block data map
     * @return block data map
     */
    @Override
    public Iterator<BlockPos> iterator() {
        return blocks.keySet().iterator();
    }

    /**
     * Gets the size of the contraption
     * @return contraption size
     */
    public Vec3i size() {
        return new Vec3i(width(), height(), depth());
    }

    /**
     * Gets the width of the contraption
     * @return contraption width
     */
    public int width() {
        int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for (BlockPos pos : this) {
			if(pos.getX() > max)
				max = pos.getX();
			else if(pos.getX() < min)
				min = pos.getX();
		}
		return max-min;
    }

    /**
     * Gets the height of the contraption
     * @return contraption height
     */
    public int height() {
        int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for (BlockPos pos : this) {
			if(pos.getY() > max)
				max = pos.getY();
			else if(pos.getY() < min)
				min = pos.getY();
		}
		return max-min;
    }

    /**
     * Gets the depth of the contraption
     * @return contraption depth
     */
    public int depth() {
        int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for (BlockPos pos : this) {
			if(pos.getZ() > max)
				max = pos.getZ();
			else if(pos.getZ() < min)
				min = pos.getZ();
		}
		return max-min;
    }

    /**
     * Serializes the contraption to NBT
     * @return NBT tag with serialized contraption
     */
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("id", id);

        CompoundTag blocksTag = new CompoundTag();
        for(var entry : blocks.entrySet()) {
            var position = entry.getKey();
            var state = entry.getValue();
            var blockTag = new CompoundTag();
            
            // Saving block state into the state tag
            var stateTag = BlockState.CODEC.encodeStart(NbtOps.INSTANCE, state).result().orElse(null);
            if(stateTag == null)
             continue;
            
            BlockState.CODEC.encode(state, NbtOps.INSTANCE, stateTag);
            blockTag.put("state", stateTag);

            // Saving the block entity into the entity tag
            if(blockEntities.containsKey(position)) {
                CompoundTag entityTag = blockEntities.get(position).saveWithFullMetadata();
                blockTag.put("blockentity", entityTag);
            }

            // Saving the block tag into the blocks tag with the position as key
            blocksTag.put(position.asLong() + "", blockTag);
        }
        tag.put("blocks", blocksTag);

        return tag;
    }

    /**
     * Deserializes the contraption from NBT
     * @param tag NBT tag to deserialize from
     */
    @Override
    public void deserializeNBT(CompoundTag tag) {
        if(tag == null || !tag.contains("id") || !tag.contains("blocks"))
            return;
        blocks.clear();
        blockEntities.clear();

        this.id = tag.getUUID("id");
        var blocksTag = tag.getCompound("blocks");
        for(String key : blocksTag.getAllKeys()) {
            var blockTag = blocksTag.getCompound(key);
            if(!blockTag.contains("state"))
                continue;
            
            // Getting the block position from the key
            var position = BlockPos.of(Long.parseLong(key));

            // Getting the block state
            var state = BlockState.CODEC.parse(NbtOps.INSTANCE, blockTag.get("state")).result().orElse(null);
            if(state == null || state.isAir())
                continue;
            
            blocks.put(position, state);

            // Getting the block entity if it exists
            if(blockTag.contains("blockentity")) {
                var entityTag = blockTag.getCompound("blockentity");
                BlockEntity blockEntity = BlockEntity.loadStatic(position, state, entityTag);
                if(blockEntity != null)
                    blockEntities.put(position, blockEntity);
            }
        }
    }

    /**
     * Creates a contraption from NBT
     * @param tag NBT tag to create contraption from
     * @return contraption created from NBT
     */
    public static Contraption from(CompoundTag tag) {
        var contraption = new Contraption();
        contraption.deserializeNBT(tag);
        return contraption;
    }
}
