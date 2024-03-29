package net.minecraft.network.login.server;

import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.IChatComponent;

import java.io.IOException;

public class S00PacketDisconnect implements IPacket<INetHandlerLoginClient>
{
    private IChatComponent reason;

    public S00PacketDisconnect()
    {
    }

    public S00PacketDisconnect(IChatComponent reasonIn)
    {
        this.reason = reasonIn;
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.reason = buf.readChatComponent();
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeChatComponent(this.reason);
    }

    public void processPacket(INetHandlerLoginClient handler)
    {
        handler.handleDisconnect(this);
    }

    public IChatComponent func_149603_c()
    {
        return this.reason;
    }
}
