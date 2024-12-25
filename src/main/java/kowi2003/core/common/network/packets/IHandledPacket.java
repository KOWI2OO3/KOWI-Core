package kowi2003.core.common.network.packets;

import java.util.function.Supplier;

import net.minecraftforge.network.NetworkEvent;

/**
 * Simular to a packet, but a handled packet handles itself when received.
 * 
 * @author KOWI2003
 */
public interface IHandledPacket extends IPacket {

    public void handle(Supplier<NetworkEvent.Context> ctx);
    
}
