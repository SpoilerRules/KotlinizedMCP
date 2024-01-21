package net.minecraft.network.play.server;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.io.IOException;

public class S23PacketBlockChange implements IPacket<INetHandlerPlayClient>
{
    private BlockPos blockPosition;
    private IBlockState blockState;

    public S23PacketBlockChange()
    {
    }

    public S23PacketBlockChange(World worldIn, BlockPos blockPositionIn)
    {
        this.blockPosition = blockPositionIn;
        this.blockState = worldIn.getBlockState(blockPositionIn);
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.blockPosition = buf.readBlockPos();
        this.blockState = (IBlockState)Block.BLOCK_STATE_IDS.getByValue(buf.readVarIntFromBuffer());
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeBlockPos(this.blockPosition);
        buf.writeVarIntToBuffer(Block.BLOCK_STATE_IDS.get(this.blockState));
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleBlockChange(this);
    }

    public IBlockState getBlockState()
    {
        return this.blockState;
    }

    public BlockPos getBlockPosition()
    {
        return this.blockPosition;
    }
}
