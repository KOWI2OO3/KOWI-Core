package kowi2003.core.network.packets.entity;

import java.util.UUID;
import java.util.function.Supplier;

import kowi2003.core.contraptions.Contraption;
import kowi2003.core.entity.entities.ContraptionEntity;
import kowi2003.core.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class PacketSyncContraption {
    
    int id;
    UUID entityId;
    Contraption contraption;
    
    public PacketSyncContraption(int id, UUID entityId, Contraption contraption) {
        this.id = id;
        this.entityId = entityId;
        this.contraption = contraption;
    }

    public PacketSyncContraption(FriendlyByteBuf buffer) {
        id = buffer.readVarInt();
        entityId = buffer.readUUID();
        contraption = Contraption.from(buffer.readNbt());
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeVarInt(id);
        buffer.writeUUID(entityId);
        buffer.writeNbt(contraption.serializeNBT());
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
                if(entity instanceof ContraptionEntity contraptionEntity)
                    contraptionEntity.setContraption(contraption);

                for(var localPlayer : level.getServer().getPlayerList().getPlayers()) {
                    if(localPlayer != player)
                        PacketHandler.sendToClient(new PacketSyncContraption(id, entityId, contraption), localPlayer);
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
        if(entity instanceof ContraptionEntity contraptionEntity)
            contraptionEntity.setContraption(contraption);
    }

}