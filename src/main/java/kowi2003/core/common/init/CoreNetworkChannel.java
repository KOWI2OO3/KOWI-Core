package kowi2003.core.common.init;

import kowi2003.core.Core;
import kowi2003.core.common.network.NetworkChannel;
import kowi2003.core.common.network.packets.entity.PacketSyncBlocks;
import kowi2003.core.common.network.packets.entity.PacketSyncContraption;
import kowi2003.core.common.network.packets.entity.PacketSyncEntity;

public final class CoreNetworkChannel {
    
    public static NetworkChannel CoreChannel = new NetworkChannel(Core.MODID)
    {{
        registerPacket(PacketSyncEntity.class, PacketSyncEntity::new);
        registerPacket(PacketSyncContraption.class, PacketSyncContraption::new);
        registerPacket(PacketSyncBlocks.class, PacketSyncBlocks::new);
    }};

}
