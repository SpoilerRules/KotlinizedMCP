package net.minecraft.network.play.server

import net.minecraft.entity.Entity
import net.minecraft.network.Packet
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.INetHandlerPlayClient
import java.io.IOException

class S12PacketEntityVelocity : Packet<INetHandlerPlayClient> {
    private var entityId: Int = 0
    private var motionX: Int = 0
    private var motionY: Int = 0
    private var motionZ: Int = 0

    constructor(entity: Entity) : this(
        entity.entityId,
        entity.motionX,
        entity.motionY,
        entity.motionZ
    )

    @Suppress("unused")
    constructor()

    constructor(entityId: Int, motionX: Double, motionY: Double, motionZ: Double) {
        this.entityId = entityId
        val maxMotion = 3.9

        motionX.coerceIn(-maxMotion, maxMotion).let { clampedMotionX ->
            motionY.coerceIn(-maxMotion, maxMotion).let { clampedMotionY ->
                motionZ.coerceIn(-maxMotion, maxMotion).let { clampedMotionZ ->
                    this.motionX = (clampedMotionX * 8000.0).toInt()
                    this.motionY = (clampedMotionY * 8000.0).toInt()
                    this.motionZ = (clampedMotionZ * 8000.0).toInt()
                }
            }
        }
    }

    @Throws(IOException::class)
    override fun readPacketData(buf: PacketBuffer) {
        entityId = buf.readVarIntFromBuffer()
        motionX = buf.readShort().toInt()
        motionY = buf.readShort().toInt()
        motionZ = buf.readShort().toInt()
    }

    @Throws(IOException::class)
    override fun writePacketData(buf: PacketBuffer) {
        buf.writeVarIntToBuffer(entityId)
        buf.writeShort(motionX)
        buf.writeShort(motionY)
        buf.writeShort(motionZ)
    }

    override fun processPacket(handler: INetHandlerPlayClient) {
        handler.handleEntityVelocity(this)
    }

    fun getEntityId(): Int = entityId
    fun getMotionX(): Int = motionX
    fun getMotionY(): Int = motionY
    fun getMotionZ(): Int = motionZ
}