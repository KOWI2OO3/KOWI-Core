package kowi2003.core.common.network;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;
import kowi2003.core.common.network.packets.IHandledPacket;
import kowi2003.core.common.network.packets.IPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    
    public SimpleChannel INSTANCE;

    private int ID = 0;

    private static final String PROTOCOL_VERSION = "1";

    int nextID() { return ID++; }

    public void registerMessages(String channelName) 
    {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(channelName),
            () -> PROTOCOL_VERSION, 
            PROTOCOL_VERSION::equals, 
            PROTOCOL_VERSION::equals);
    }

    /**
     * Registers a handled packet for this channel, the handled packet has its own handle method
     * @param <T> the packet type
     * @param packetType the class of the packet
     * @param creator a supplier which creates a new packet object from the byte buffer
     */
    protected <T extends IHandledPacket> void registerPacket(Class<T> packetType, Function<FriendlyByteBuf, T> creator) 
    {
        INSTANCE.messageBuilder(packetType, nextID())
			.encoder(IPacket::toBytes)
			.decoder(creator)
			.consumerMainThread(IHandledPacket::handle)
			.add();
    }

    /**
     * Registers a packet for this channel.
     * @param <T> the packet type
     * @param packetType the class of the packet
     * @param creator a supplier which creates a new packet object from the byte buffer
     * @param handle the method called to handle the packet (both sides)
     */
    protected <T extends IPacket> void registerPacket(Class<T> packetType, Function<FriendlyByteBuf, T> creator, BiConsumer<T, Supplier<NetworkEvent.Context>> handle) 
    {
        INSTANCE.messageBuilder(packetType, nextID())
			.encoder(IPacket::toBytes)
			.decoder(creator)
			.consumerMainThread(handle)
			.add();
    }

    /**
     * Registers a packet for this channel. Handled on the client, send from the server.
     * @param <T> the packet type
     * @param packetType the class of the packet
     * @param creator a supplier which creates a new packet object from the byte buffer
     * @param handle the method called to handle the packet (client side only)
     */
    protected <T extends IPacket> void registerClientHandlePacket(Class<T> packetType, Function<FriendlyByteBuf, T> creator, Consumer<T> handle) 
    {
        INSTANCE.messageBuilder(packetType, nextID())
			.encoder(IPacket::toBytes)
			.decoder(creator)
			.consumerMainThread((packet, ctx) -> {
                ctx.get().enqueueWork(() -> {
                    if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
                        handle.accept(packet);
                });
                ctx.get().setPacketHandled(true);
            })
			.add();
    }

    /**
     * Registers a packet for this channel. Handled on the server, send from the client.
     * @param <T> the packet type
     * @param packetType the class of the packet
     * @param creator a supplier which creates a new packet object from the byte buffer
     * @param handle the method called to handle the packet (server side only)
     */
    protected <T extends IPacket> void registerServerHandlePacket(Class<T> packetType, Function<FriendlyByteBuf, T> creator, BiConsumer<T, NetworkEvent.Context> handle) 
    {
        INSTANCE.messageBuilder(packetType, nextID())
			.encoder(IPacket::toBytes)
			.decoder(creator)
			.consumerMainThread((packet, ctx) -> {
                ctx.get().enqueueWork(() -> {
                    if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
                        handle.accept(packet, ctx.get());
                });
                ctx.get().setPacketHandled(true);
            })
			.add();
    }

    /**
     * Sends a packet to all clients (Only called on server side)
     * @param packet the packet to send to all clients
     * @param level the level to send the packet to
     */
    public void sendToAllClients(Object packet, Level level) {
		if(!level.isClientSide)
			for (Player player : level.players())
				sendToClient(packet, (ServerPlayer)player);
	}
	
    /**
     * Sends a packet to all clients in a the defined bounding box (Only called on server side)
     * @param packet the packet to send to all clients
     * @param level the level to send the packet to
     * @param bounds the bounding box to send the packet to
     */
	public void sendToAllClientsInRange(Object packet, Level level, AABB bounds) {
		if(!level.isClientSide) {
			List<Player> players = level.getEntitiesOfClass(Player.class, bounds);
			for (Player player : players)
				sendToClient(packet, (ServerPlayer)player);
		}
	}
	
    /**
     * Sends a packet to all clients in a certain range (Only called on server side)
     * @param packet the packet to send to all clients
     * @param level the level to send the packet to
     * @param pos the position to send the packet to
     * @param range the range to send the packet to
     */
	public void sendToAllClientsInRange(Object packet, Level level, BlockPos pos, float range) {
		sendToAllClientsInRange(packet, level, new AABB(pos).inflate(range));
	}
	
    /**
     * Sends a packet to a specific client/player (Only called on server side)
     * @param packet the packet to send to the client
     * @param player the player to send the packet to
     */
	public void sendToClient(Object packet, ServerPlayer player) {
    	if(INSTANCE != null && packet != null)
    		INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    /**
     * Sends a packet to the server (Only called on the client side)
     * @param packet the packet to send to the server
     */
    public void sendToServer(Object packet) {
    	if(INSTANCE != null && packet != null)
    		INSTANCE.sendToServer(packet);
    }

}
