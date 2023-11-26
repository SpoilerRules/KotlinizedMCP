package net.optifine.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vector3D;
import org.lwjgl.opengl.GL11;

public class CloudRenderer
{
    private Minecraft mc;
    private boolean updated = false;
    private boolean renderFancy = false;
    int cloudTickCounter;
    private Vector3D cloudColor;
    float partialTicks;
    private boolean updateRenderFancy = false;
    private int updateCloudTickCounter = 0;
    private Vector3D updateCloudColor = new Vector3D(-1.0D, -1.0D, -1.0D);
    private double updatePlayerX = 0.0D;
    private double updatePlayerY = 0.0D;
    private double updatePlayerZ = 0.0D;
    private int glListClouds = -1;

    public CloudRenderer(Minecraft mc)
    {
        this.mc = mc;
        this.glListClouds = GLAllocation.generateDisplayLists(1);
    }

    public void prepareToRender(boolean renderFancy, int cloudTickCounter, float partialTicks, Vector3D cloudColor)
    {
        this.renderFancy = renderFancy;
        this.cloudTickCounter = cloudTickCounter;
        this.partialTicks = partialTicks;
        this.cloudColor = cloudColor;
    }

    public boolean shouldUpdateGlList()
    {
        if (!this.updated || this.renderFancy != this.updateRenderFancy || this.cloudTickCounter >= this.updateCloudTickCounter + 20)
        {
            return true;
        }

        double diffX = Math.abs(this.cloudColor.x - this.updateCloudColor.x);
        double diffY = Math.abs(this.cloudColor.y - this.updateCloudColor.y);
        double diffZ = Math.abs(this.cloudColor.z - this.updateCloudColor.z);

        if (diffX > 0.003D || diffY > 0.003D || diffZ > 0.003D)
        {
            return true;
        }

        Entity entity = this.mc.getRenderViewEntity();
        double eyeHeight = entity.getEyeHeight();
        double cloudHeight = 128.0D + this.mc.gameSettings.ofCloudsHeight * 128.0F;

        boolean flag = this.updatePlayerY + eyeHeight < cloudHeight;
        boolean flag1 = entity.prevPosY + eyeHeight < cloudHeight;

        return flag1 != flag;
    }

    public void startUpdateGlList()
    {
        GL11.glNewList(this.glListClouds, GL11.GL_COMPILE);
    }

    public void endUpdateGlList()
    {
        GL11.glEndList();
        this.updateRenderFancy = this.renderFancy;
        this.updateCloudTickCounter = this.cloudTickCounter;
        this.updateCloudColor = this.cloudColor;
        this.updatePlayerX = this.mc.getRenderViewEntity().prevPosX;
        this.updatePlayerY = this.mc.getRenderViewEntity().prevPosY;
        this.updatePlayerZ = this.mc.getRenderViewEntity().prevPosZ;
        this.updated = true;
        GlStateManager.resetColor();
    }

    public void renderGlList()
    {
        Entity entity = this.mc.getRenderViewEntity();
        double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * this.partialTicks;
        double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * this.partialTicks;
        double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * this.partialTicks;
        double d3 = this.cloudTickCounter - this.updateCloudTickCounter + this.partialTicks;
        float f = (float)(d0 - this.updatePlayerX + d3 * 0.03D);
        float f1 = (float)(d1 - this.updatePlayerY);
        float f2 = (float)(d2 - this.updatePlayerZ);

        GlStateManager.pushMatrix();

        if (this.renderFancy)
        {
            GlStateManager.translate(-f / 12.0F, -f1, -f2 / 12.0F);
        }
        else
        {
            GlStateManager.translate(-f, -f1, -f2);
        }

        GlStateManager.callList(this.glListClouds);
        GlStateManager.popMatrix();
        GlStateManager.resetColor();
    }

    public void reset()
    {
        this.updated = false;
    }
}
