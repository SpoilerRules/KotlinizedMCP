package net.minecraft.network.play.client;

import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

import java.io.IOException;

public class C00PacketKeepAlive implements IPacket<INetHandlerPlayServer>
{
    private int key;

    public C00PacketKeepAlive()
    {
    }

    public C00PacketKeepAlive(int key)
    {
        this.key = key;
    }

    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processKeepAlive(this);
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.key = buf.readVarIntFromBuffer();
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.key);
    }

    public int getKey()
    {
        return this.key;
    }
}
