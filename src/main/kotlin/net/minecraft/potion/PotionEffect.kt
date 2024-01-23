package net.minecraft.potion

import net.minecraft.entity.EntityLivingBase
import net.minecraft.nbt.NBTTagCompound
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

data class PotionEffect @JvmOverloads constructor(
    val potionID: Int,
    var duration: Int,
    var amplifier: Int = 0,
    var isAmbient: Boolean = false,
    var isShowParticles: Boolean = true,
    var isSplashPotion: Boolean = false,
    var isPotionDurationMax: Boolean = false
) {

    constructor(other: PotionEffect) : this(
        other.potionID,
        other.duration,
        other.amplifier,
        other.isAmbient,
        other.isShowParticles,
        other.isSplashPotion,
        other.isPotionDurationMax
    )

    val effectName: String
        get() = Potion.potionTypes[potionID].name

    fun combine(other: PotionEffect) {
        if (potionID != other.potionID) {
            LOGGER.warn("This method should only be called for matching effects!")
            return
        }

        with(other) {
            if (amplifier > this@PotionEffect.amplifier || (amplifier == this@PotionEffect.amplifier && duration > this@PotionEffect.duration)) {
                amplifier = this@PotionEffect.amplifier
                duration = this@PotionEffect.duration
            } else if (!isAmbient && this@PotionEffect.isAmbient) {
                isAmbient = false
            }

            isShowParticles = this@PotionEffect.isShowParticles
            isSplashPotion = this@PotionEffect.isSplashPotion
            isPotionDurationMax = this@PotionEffect.isPotionDurationMax
        }
    }

    fun updatePotionDuration(livingEntity: EntityLivingBase): Boolean {
        if (duration <= 0) return false

        Potion.potionTypes[potionID].let { potion ->
            if (potion.isReady(duration, amplifier)) {
                performEffect(livingEntity)
            }
        }

        return --duration > 0
    }

    private fun performEffect(entityIn: EntityLivingBase) {
        if (duration > 0) {
            Potion.potionTypes[potionID].performEffect(entityIn, amplifier)
        }
    }

    fun writeCustomPotionEffectToNBT(nbt: NBTTagCompound): NBTTagCompound =
        nbt.apply {
            setByte("Id", potionID.toByte())
            setByte("Amplifier", amplifier.toByte())
            setInteger("Duration", duration)
            setBoolean("Ambient", isAmbient)
            setBoolean("ShowParticles", isShowParticles)
            setBoolean("IsSplashPotion", isSplashPotion)
            setBoolean("IsPotionDurationMax", isPotionDurationMax)
        }

    companion object {
        private val LOGGER: Logger = LogManager.getLogger()

        fun readCustomPotionEffectFromNBT(nbt: NBTTagCompound): PotionEffect? {
            val id = nbt.getByte("Id").toInt()

            return if (id in 0 until Potion.potionTypes.size && Potion.potionTypes[id] != null) {
                with(nbt) {
                    val amplifier = getByte("Amplifier").toInt()
                    val duration = getInteger("Duration")
                    val ambient = getBoolean("Ambient")
                    val showParticles = getBoolean("ShowParticles")
                    val isSplashPotion = getBoolean("IsSplashPotion")
                    val isPotionDurationMax = getBoolean("IsPotionDurationMax")

                    PotionEffect(id, duration, amplifier, ambient, showParticles, isSplashPotion, isPotionDurationMax)
                }
            } else {
                null
            }
        }
    }
}