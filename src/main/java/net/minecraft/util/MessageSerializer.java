package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.IOException;

public class MessageSerializer extends MessageToByteEncoder<IPacket>
{
    private static final Logger logger = LogManager.getLogger();
    Marker logMarkerPackets = MarkerManager.getMarker("LOG_MARKER_PACKETS");
    Marker RECEIVED_PACKET_MARKER = MarkerManager.getMarker("PACKET_SENT").addParents(logMarkerPackets);
    private final EnumPacketDirection direction;

    public MessageSerializer(EnumPacketDirection direction)
    {
        this.direction = direction;
    }

    protected void encode(ChannelHandlerContext p_encode_1_, IPacket p_encode_2_, ByteBuf p_encode_3_) throws IOException, Exception
    {
        Integer integer = ((EnumConnectionState)p_encode_1_.channel().attr(NetworkManager.attrKeyConnectionState).get()).getPacketId(this.direction, p_encode_2_);

        if (logger.isDebugEnabled())
        {
            logger.debug(RECEIVED_PACKET_MARKER, "OUT: [{}:{}] {}", new Object[] {p_encode_1_.channel().attr(NetworkManager.attrKeyConnectionState).get(), integer, p_encode_2_.getClass().getName()});
        }

        if (integer == null)
        {
            throw new IOException("Can\'t serialize unregistered packet");
        }
        else
        {
            PacketBuffer packetbuffer = new PacketBuffer(p_encode_3_);
            packetbuffer.writeVarIntToBuffer(integer.intValue());

            try
            {
                p_encode_2_.writePacketData(packetbuffer);
            }
            catch (Throwable throwable)
            {
                logger.error((Object)throwable);
            }
        }
    }
}
