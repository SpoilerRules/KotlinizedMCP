package net.minecraft.network.play.client;

import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.Vector3D;
import net.minecraft.world.World;

import java.io.IOException;

public class C02PacketUseEntity implements IPacket<INetHandlerPlayServer>
{
    private int entityId;
    private C02PacketUseEntity.Action action;
    private Vector3D hitVec;

    public C02PacketUseEntity()
    {
    }

    public C02PacketUseEntity(Entity entity, C02PacketUseEntity.Action action)
    {
        this.entityId = entity.getEntityId();
        this.action = action;
    }

    public C02PacketUseEntity(Entity entity, Vector3D hitVec)
    {
        this(entity, C02PacketUseEntity.Action.INTERACT_AT);
        this.hitVec = hitVec;
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityId = buf.readVarIntFromBuffer();
        this.action = (C02PacketUseEntity.Action)buf.readEnumValue(C02PacketUseEntity.Action.class);

        if (this.action == C02PacketUseEntity.Action.INTERACT_AT)
        {
            this.hitVec = new Vector3D((double)buf.readFloat(), (double)buf.readFloat(), (double)buf.readFloat());
        }
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.entityId);
        buf.writeEnumValue(this.action);

        if (this.action == C02PacketUseEntity.Action.INTERACT_AT)
        {
            buf.writeFloat((float)this.hitVec.x);
            buf.writeFloat((float)this.hitVec.y);
            buf.writeFloat((float)this.hitVec.z);
        }
    }

    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processUseEntity(this);
    }

    public Entity getEntityFromWorld(World worldIn)
    {
        return worldIn.getEntityByID(this.entityId);
    }

    public C02PacketUseEntity.Action getAction()
    {
        return this.action;
    }

    public Vector3D getHitVec()
    {
        return this.hitVec;
    }

    public enum Action
    {
        INTERACT,
        ATTACK,
        INTERACT_AT
    }
}
