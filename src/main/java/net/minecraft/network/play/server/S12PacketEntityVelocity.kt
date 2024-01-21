package net.minecraft.network.play.server

import net.minecraft.entity.Entity
import net.minecraft.network.IPacket
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.INetHandlerPlayClient

data class S12PacketEntityVelocity(
    var entityId: Int = 0,
    var motionX: Int = 0,
    var motionY: Int = 0,
    var motionZ: Int = 0
) : IPacket<INetHandlerPlayClient> {

    constructor(entity: Entity) : this(
        entity.entityId,
        entity.motionX,
        entity.motionY,
        entity.motionZ
    )

    constructor(entityId: Int, motionX: Double, motionY: Double, motionZ: Double) : this(
        entityId,
        (motionX.coerceIn(-3.9, 3.9) * 8000.0).toInt(),
        (motionY.coerceIn(-3.9, 3.9) * 8000.0).toInt(),
        (motionZ.coerceIn(-3.9, 3.9) * 8000.0).toInt()
    )

    override fun readPacketData(buf: PacketBuffer) {
        entityId = buf.readVarIntFromBuffer()
        motionX = buf.readInt()
        motionY = buf.readInt()
        motionZ = buf.readInt()
    }

    override fun writePacketData(buf: PacketBuffer) {
        buf.writeVarIntToBuffer(entityId)
        buf.writeInt(motionX)
        buf.writeInt(motionY)
        buf.writeInt(motionZ)
    }

    override fun processPacket(handler: INetHandlerPlayClient) {
        handler.handleEntityVelocity(this)
    }
}