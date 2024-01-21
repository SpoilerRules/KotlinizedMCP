package net.minecraft.network.play.server;

import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

import java.io.IOException;

public class S00PacketKeepAlive implements IPacket<INetHandlerPlayClient>
{
    private int id;

    public S00PacketKeepAlive()
    {
    }

    public S00PacketKeepAlive(int idIn)
    {
        this.id = idIn;
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleKeepAlive(this);
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.id = buf.readVarIntFromBuffer();
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.id);
    }

    public int func_149134_c()
    {
        return this.id;
    }
}
