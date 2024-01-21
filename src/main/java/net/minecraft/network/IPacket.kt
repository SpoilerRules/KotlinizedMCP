package net.minecraft.network

import java.io.IOException

/**
 * Represents a network packet.
 *
 * @param T the type of the handler used to process this packet
 */
interface IPacket<T : INetHandler> {
    /**
     * Reads the packet data from the given buffer.
     *
     * @param buf the buffer to read from
     * @throws IOException if an I/O error occurs
     */
    @Throws(IOException::class)
    fun readPacketData(buf: PacketBuffer)

    /**
     * Writes the packet data to the given buffer.
     *
     * @param buf the buffer to write to
     * @throws IOException if an I/O error occurs
     */
    @Throws(IOException::class)
    fun writePacketData(buf: PacketBuffer)

    /**
     * Processes this packet using the given handler.
     *
     * @param handler the handler to use
     */
    fun processPacket(handler: T)
}