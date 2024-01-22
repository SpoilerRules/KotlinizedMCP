package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PotionEffect {
    private static final Logger LOGGER = LogManager.getLogger();

    private final int potionID;
    private int duration;
    private int amplifier;
    private boolean isSplashPotion;
    private boolean isAmbient;
    private boolean showParticles;
    private boolean isPotionDurationMax;

    public PotionEffect(int id, int effectDuration) {
        this(id, effectDuration, 0);
    }

    public PotionEffect(int id, int effectDuration, int effectAmplifier) {
        this(id, effectDuration, effectAmplifier, false, true);
    }

    public PotionEffect(int id, int effectDuration, int effectAmplifier, boolean ambient, boolean showParticles) {
        this.potionID = id;
        this.duration = effectDuration;
        this.amplifier = effectAmplifier;
        this.isAmbient = ambient;
        this.showParticles = showParticles;
    }

    public PotionEffect(PotionEffect other) {
        this(other.potionID, other.duration, other.amplifier, other.isAmbient, other.showParticles);
        this.isSplashPotion = other.isSplashPotion;
        this.isPotionDurationMax = other.isPotionDurationMax;
    }

    public void combine(PotionEffect other) {
        if (this.potionID != other.potionID) {
            LOGGER.warn("This method should only be called for matching effects!");
            return;
        }

        if (other.amplifier > this.amplifier || (other.amplifier == this.amplifier && other.duration > this.duration)) {
            this.amplifier = other.amplifier;
            this.duration = other.duration;
        } else if (!other.isAmbient && this.isAmbient) {
            this.isAmbient = false;
        }

        this.showParticles = other.showParticles;
        this.isSplashPotion = other.isSplashPotion;
        this.isPotionDurationMax = other.isPotionDurationMax;
    }

    public int getPotionID() {
        return potionID;
    }

    public int getDuration() {
        return duration;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public void setSplashPotion(boolean splashPotion) {
        isSplashPotion = splashPotion;
    }

    public boolean isAmbient() {
        return isAmbient;
    }

    public boolean isShowParticles() {
        return showParticles;
    }

    public boolean onUpdate(EntityLivingBase entityIn) {
        if (duration > 0 && Potion.potionTypes[potionID].isReady(duration, amplifier)) {
            performEffect(entityIn);
            decreaseDuration();
        }

        return duration > 0;
    }

    private void decreaseDuration() {
        duration--;
    }

    public void performEffect(EntityLivingBase entityIn) {
        if (duration > 0) {
            Potion.potionTypes[potionID].performEffect(entityIn, amplifier);
        }
    }

    public String getEffectName() {
        return Potion.potionTypes[potionID].getName();
    }

    @Override
    public int hashCode() {
        return potionID;
    }

    @Override
    public String toString() {
        String s = (amplifier > 0) ? getEffectName() + " x " + (amplifier + 1) + ", Duration: " + duration :
                getEffectName() + ", Duration: " + duration;

        if (isSplashPotion) {
            s += ", Splash: true";
        }

        if (!showParticles) {
            s += ", Particles: false";
        }

        return Potion.potionTypes[potionID].isUsable() ? "(" + s + ")" : s;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PotionEffect other)) return false;

        return potionID == other.potionID && amplifier == other.amplifier && duration == other.duration
                && isSplashPotion == other.isSplashPotion && isAmbient == other.isAmbient;
    }

    public NBTTagCompound writeCustomPotionEffectToNBT(NBTTagCompound nbt) {
        nbt.setByte("Id", (byte) getPotionID());
        nbt.setByte("Amplifier", (byte) getAmplifier());
        nbt.setInteger("Duration", getDuration());
        nbt.setBoolean("Ambient", isAmbient());
        nbt.setBoolean("ShowParticles", isShowParticles());
        nbt.setBoolean("IsSplashPotion", isSplashPotion);
        nbt.setBoolean("IsPotionDurationMax", isPotionDurationMax);
        return nbt;
    }

    public static PotionEffect readCustomPotionEffectFromNBT(NBTTagCompound nbt) {
        int id = nbt.getByte("Id");

        if (id >= 0 && id < Potion.potionTypes.length && Potion.potionTypes[id] != null) {
            int amplifier = nbt.getByte("Amplifier");
            int duration = nbt.getInteger("Duration");
            boolean ambient = nbt.getBoolean("Ambient");
            boolean showParticles = nbt.getBoolean("ShowParticles");
            boolean isSplashPotion = nbt.getBoolean("IsSplashPotion");
            boolean isPotionDurationMax = nbt.getBoolean("IsPotionDurationMax");

            PotionEffect result = new PotionEffect(id, duration, amplifier, ambient, showParticles);
            result.isSplashPotion = isSplashPotion;
            result.isPotionDurationMax = isPotionDurationMax;

            return result;
        } else {
            return null;
        }
    }

    public void setPotionDurationMax(boolean maxDuration) {
        isPotionDurationMax = maxDuration;
    }

    public boolean isPotionDurationMax() {
        return isPotionDurationMax;
    }
}