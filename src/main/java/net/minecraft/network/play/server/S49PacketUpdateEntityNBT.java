package net.minecraft.network.play.server;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

import java.io.IOException;

public class S49PacketUpdateEntityNBT implements IPacket<INetHandlerPlayClient>
{
    private int entityId;
    private NBTTagCompound tagCompound;

    public S49PacketUpdateEntityNBT()
    {
    }

    public S49PacketUpdateEntityNBT(int entityIdIn, NBTTagCompound tagCompoundIn)
    {
        this.entityId = entityIdIn;
        this.tagCompound = tagCompoundIn;
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityId = buf.readVarIntFromBuffer();
        this.tagCompound = buf.readNBTTagCompoundFromBuffer();
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.entityId);
        buf.writeNBTTagCompoundToBuffer(this.tagCompound);
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleEntityNBT(this);
    }

    public NBTTagCompound getTagCompound()
    {
        return this.tagCompound;
    }

    public Entity getEntity(World worldIn)
    {
        return worldIn.getEntityByID(this.entityId);
    }
}
