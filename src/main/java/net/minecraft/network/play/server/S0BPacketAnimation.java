package net.minecraft.network.play.server;

import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

import java.io.IOException;

public class S0BPacketAnimation implements IPacket<INetHandlerPlayClient>
{
    private int entityId;
    private int type;

    public S0BPacketAnimation()
    {
    }

    public S0BPacketAnimation(Entity ent, int animationType)
    {
        this.entityId = ent.getEntityId();
        this.type = animationType;
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityId = buf.readVarIntFromBuffer();
        this.type = buf.readUnsignedByte();
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.entityId);
        buf.writeByte(this.type);
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleAnimation(this);
    }

    public int getEntityID()
    {
        return this.entityId;
    }

    public int getAnimationType()
    {
        return this.type;
    }
}
