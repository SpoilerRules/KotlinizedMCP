package net.minecraft.network.play.server

import net.minecraft.entity.Entity
import net.minecraft.network.Packet
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.INetHandlerPlayClient

data class S12PacketEntityVelocity(
    var entityId: Int = 0,
    var motionX: Int = 0,
    var motionY: Int = 0,
    var motionZ: Int = 0
) : Packet<INetHandlerPlayClient> {

    constructor(entity: Entity) : this(
        entity.entityId,
        (entity.motionX.coerceIn(-3.9, 3.9) * 8000.0).toInt(),
        (entity.motionY.coerceIn(-3.9, 3.9) * 8000.0).toInt(),
        (entity.motionZ.coerceIn(-3.9, 3.9) * 8000.0).toInt()
    )

    override fun readPacketData(buf: PacketBuffer) {
        entityId = buf.readVarIntFromBuffer()
        motionX = buf.readShort().toInt()
        motionY = buf.readShort().toInt()
        motionZ = buf.readShort().toInt()
    }

    override fun writePacketData(buf: PacketBuffer) {
        buf.writeVarIntToBuffer(entityId)
        buf.writeShort(motionX)
        buf.writeShort(motionY)
        buf.writeShort(motionZ)
    }

    override fun processPacket(handler: INetHandlerPlayClient) {
        handler.handleEntityVelocity(this)
    }
}