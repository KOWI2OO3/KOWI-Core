package kowi2003.core.common.blockentities;

import javax.annotation.Nullable;

import kowi2003.core.common.helpers.BlockEntityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Syncable Block Entity, a simple block entity extension allowing for a block entity to
 * be synced to the clients by calling a single sync method
 * 
 * @author KOWI2003
 */
public class SyncableBlockEntity extends BlockEntity {

    /**
     * Creates a new syncable block entity
     * @param type the type of the block entity
     * @param position the position of the block entity
     * @param state the state of the block this block entity is linked to
     */
    public SyncableBlockEntity(BlockEntityType<?> type, BlockPos position, BlockState state) {
        super(type, position, state);
    }
    
    /**
     * Gets the update packet for this block entity, 
     * to be send when this block needs to sync to the client
     */
    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::serializeNBT);
    }

    /**
     * Gets the nbt tag that should be used to sync this block entity
     */
    @Override
    public CompoundTag getUpdateTag() {
        return serializeNBT();
    }

    /**
     * handles the incomming nbt tag from syncing 
     */
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if(tag != null)
            this.load(tag);
    }

    /**
     * Gets the nbt data which defines what data should be persisted (saved)
     */
    @Override
    public CompoundTag getPersistentData() {
        return this.serializeNBT();
    }

    /**
     * Syncs this block entity to the clients
     */
    public void sync() {
        BlockEntityHelper.syncToClient(this);
    }

}
