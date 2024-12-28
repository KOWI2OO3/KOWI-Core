package kowi2003.core.common.network.packets;

import net.minecraft.network.FriendlyByteBuf;

public interface IPacket {

    /**
     * Writes the packet to the bytebuffer supplied
     * @param buffer the buffer to write the packet to
     */
    void toBytes(FriendlyByteBuf buffer);

}
