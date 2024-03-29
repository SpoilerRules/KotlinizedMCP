package net.minecraft.network.play.server;

import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

import java.io.IOException;

public class S13PacketDestroyEntities implements IPacket<INetHandlerPlayClient>
{
    private int[] entityIDs;

    public S13PacketDestroyEntities()
    {
    }

    public S13PacketDestroyEntities(int... entityIDsIn)
    {
        this.entityIDs = entityIDsIn;
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityIDs = new int[buf.readVarIntFromBuffer()];

        for (int i = 0; i < this.entityIDs.length; ++i)
        {
            this.entityIDs[i] = buf.readVarIntFromBuffer();
        }
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.entityIDs.length);

        for (int i = 0; i < this.entityIDs.length; ++i)
        {
            buf.writeVarIntToBuffer(this.entityIDs[i]);
        }
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleDestroyEntities(this);
    }

    public int[] getEntityIDs()
    {
        return this.entityIDs;
    }
}
