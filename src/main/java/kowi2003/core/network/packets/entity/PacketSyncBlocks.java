package kowi2003.core.network.packets.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import kowi2003.core.contraptions.BlockData;
import kowi2003.core.entity.entities.ContraptionEntity;
import kowi2003.core.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class PacketSyncBlocks {
     
    int id;
    UUID entityId;
    Set<BlockData> blocks;
    
    public PacketSyncBlocks(int id, UUID entityId, Set<BlockData> blocks) {
        this.id = id;
        this.entityId = entityId;
        this.blocks = blocks;
    }

    public PacketSyncBlocks(FriendlyByteBuf buffer) {
        id = buffer.readVarInt();
        entityId = buffer.readUUID();

        blocks = new HashSet<BlockData>();
        int count = buffer.readVarInt();
        for(int i = 0; i < count; i++)
            blocks.add(BlockData.from(buffer.readNbt()));
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeVarInt(id);
        buffer.writeUUID(entityId);
        buffer.writeVarInt(blocks.size());
        for(var block : blocks)
            buffer.writeNbt(block.serializeNBT());
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
                    handleUpdate(entity);

                for(var localPlayer : level.getServer().getPlayerList().getPlayers()) {
                    if(localPlayer != player)
                        PacketHandler.sendToClient(new PacketSyncBlocks(id, entityId, blocks), localPlayer);
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
            handleUpdate(entity);
    }

    private void handleUpdate(@Nonnull Entity entity) {
        if(entity instanceof ContraptionEntity contraption) {
            for(var block : blocks) {
                if(block != null)
                   contraption.contraption().setBlock(block.position(), block.state(), block.blockEntity());
            }
        }
    }
}