package kowi2003.core.common.network;

import java.util.List;

import kowi2003.core.common.network.packets.entity.PacketSyncBlocks;
import kowi2003.core.common.network.packets.entity.PacketSyncContraption;
import kowi2003.core.common.network.packets.entity.PacketSyncEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    
	public static SimpleChannel INSTANCE;

	private static int ID = 0;

	private static final String PROTOCOL_VERSION = "1";
	
	private static int nextID() {
		return ID++;
	}

	/**
	 * Register all of our network messages on their appropriate side
	 * 
	 * @param channelName
	 *            The name of the network channel
	 */
	public static void registerMessages(String channelName) {
		INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(channelName), 
				() -> PROTOCOL_VERSION,
			    PROTOCOL_VERSION::equals,
			    PROTOCOL_VERSION::equals);

		INSTANCE.messageBuilder(PacketSyncEntity.class, nextID())
		.encoder(PacketSyncEntity::toBytes)
		.decoder(PacketSyncEntity::new)
		.consumerNetworkThread(PacketSyncEntity::handle)
		.add();

		INSTANCE.messageBuilder(PacketSyncContraption.class, nextID())
		.encoder(PacketSyncContraption::toBytes)
		.decoder(PacketSyncContraption::new)
		.consumerNetworkThread(PacketSyncContraption::handle)
		.add();
		
		INSTANCE.messageBuilder(PacketSyncBlocks.class, nextID())
		.encoder(PacketSyncBlocks::toBytes)
		.decoder(PacketSyncBlocks::new)
		.consumerNetworkThread(PacketSyncBlocks::handle)
		.add();
	}
	
	public static void sendToAllClients(Object packet, Level level) {
		if(!level.isClientSide)
			for (Player player : level.players())
				sendToClient(packet, (ServerPlayer)player);
	}
	
	public static void sendToAllClientsInRange(Object packet, Level level, AABB bounds) {
		if(!level.isClientSide) {
			List<Player> players = level.getEntitiesOfClass(Player.class, bounds);
			for (Player player : players)
				sendToClient(packet, (ServerPlayer)player);
		}
	}
	
	public static void sendToAllClientsInRange(Object packet, Level level, BlockPos pos, float range) {
		sendToAllClientsInRange(packet, level, new AABB(pos).inflate(range));
	}
	
	public static void sendToClient(Object packet, ServerPlayer player) {
    	if(INSTANCE != null && packet != null)
    		INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object packet) {
    	if(INSTANCE != null && packet != null)
    		INSTANCE.sendToServer(packet);
    }
	
}
