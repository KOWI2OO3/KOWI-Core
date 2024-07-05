package kowi2003.core.network.packets.entity;

import java.util.UUID;
import java.util.function.Supplier;

import kowi2003.core.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class PacketSyncEntity {
    
    int id;
    UUID entityId;
    CompoundTag entityData;
    
    public PacketSyncEntity(int id, UUID entityId, CompoundTag entityData) {
        this.id = id;
        this.entityId = entityId;
        this.entityData = entityData;
    }

    public PacketSyncEntity(FriendlyByteBuf buffer) {
        id = buffer.readVarInt();
        entityId = buffer.readUUID();
        entityData = buffer.readNbt();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeVarInt(id);
        buffer.writeUUID(entityId);
        buffer.writeNbt(entityData);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                // Handle packet on client
                handleClient();
            } else {
                // Handle packet on server
                var player = ctx.get().getSender();
                var level = (ServerLevel)player.level();

                var entity = level.getEntity(entityId);
                if(entity != null)
                    entity.load(entityData);

                for(var localPlayer : level.getServer().getPlayerList().getPlayers()) {
                    if(localPlayer != player)
                        PacketHandler.sendToClient(new PacketSyncEntity(id, entityId, entityData), localPlayer);
                }
            }
        });
	    ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleClient() {
        // Handle packet on client
        var mc = Minecraft.getInstance();
        var player = mc.player;
        if(player == null) return;

        var entity = player.level().getEntity(id);
        if(entity != null)
            entity.load(entityData);
        // var entities = mc.player.level().getEntitiesOfClass(Entity.class, mc.player.getBoundingBox().inflate(50));
    }
}
