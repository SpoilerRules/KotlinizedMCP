package net.minecraft.network.play.server;

import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;

import java.io.IOException;

public class S36PacketSignEditorOpen implements IPacket<INetHandlerPlayClient>
{
    private BlockPos signPosition;

    public S36PacketSignEditorOpen()
    {
    }

    public S36PacketSignEditorOpen(BlockPos signPositionIn)
    {
        this.signPosition = signPositionIn;
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleSignEditorOpen(this);
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.signPosition = buf.readBlockPos();
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeBlockPos(this.signPosition);
    }

    public BlockPos getSignPosition()
    {
        return this.signPosition;
    }
}
