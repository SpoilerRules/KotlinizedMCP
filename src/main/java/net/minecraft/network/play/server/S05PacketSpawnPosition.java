package net.minecraft.network.play.server;

import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;

import java.io.IOException;

public class S05PacketSpawnPosition implements IPacket<INetHandlerPlayClient>
{
    private BlockPos spawnBlockPos;

    public S05PacketSpawnPosition()
    {
    }

    public S05PacketSpawnPosition(BlockPos spawnBlockPosIn)
    {
        this.spawnBlockPos = spawnBlockPosIn;
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.spawnBlockPos = buf.readBlockPos();
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeBlockPos(this.spawnBlockPos);
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleSpawnPosition(this);
    }

    public BlockPos getSpawnPos()
    {
        return this.spawnBlockPos;
    }
}
