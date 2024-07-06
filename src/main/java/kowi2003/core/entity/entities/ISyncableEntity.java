package kowi2003.core.entity.entities;

import java.util.UUID;

import javax.annotation.Nonnull;

import kowi2003.core.network.PacketHandler;
import kowi2003.core.network.packets.entity.PacketSyncEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public interface ISyncableEntity {

    boolean save(@Nonnull CompoundTag tag);
    UUID getUUID();
    int getId();
    Level level();

    default void sync() {
        // Sync entity
        var tag = new CompoundTag();
        save(tag);
        if(level().isClientSide())
            PacketHandler.sendToServer(new PacketSyncEntity(getId(), getUUID(), tag));
        else 
            PacketHandler.sendToAllClients(new PacketSyncEntity(getId(), getUUID(), tag), level());
    }

}
