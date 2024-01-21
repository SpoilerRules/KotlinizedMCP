package net.minecraft.network.play.client;

import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

import java.io.IOException;

public class C09PacketHeldItemChange implements IPacket<INetHandlerPlayServer>
{
    private int slotId;

    public C09PacketHeldItemChange()
    {
    }

    public C09PacketHeldItemChange(int slotId)
    {
        this.slotId = slotId;
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.slotId = buf.readShort();
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeShort(this.slotId);
    }

    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processHeldItemChange(this);
    }

    public int getSlotId()
    {
        return this.slotId;
    }
}
