package net.minecraft.client.resources;

import com.google.gson.JsonParseException;
import java.io.IOException;

import net.minecraft.client.CommonResourceElement;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackListEntryDefault extends ResourcePackListEntry
{
    private static final Logger logger = LogManager.getLogger();
    private final IResourcePack field_148320_d;
    private final ResourceLocation resourcePackIcon;

    public ResourcePackListEntryDefault(GuiScreenResourcePacks resourcePacksGUIIn)
    {
        super(resourcePacksGUIIn);
        this.field_148320_d = CommonResourceElement.Companion.getResourcePackRepository().rprDefaultResourcePack;
        DynamicTexture dynamictexture;

        try
        {
            dynamictexture = new DynamicTexture(this.field_148320_d.getPackImage());
        }
        catch (IOException var4)
        {
            dynamictexture = TextureUtil.missingTexture;
        }

        this.resourcePackIcon = this.mc.getTextureManager().getDynamicTextureLocation("texturepackicon", dynamictexture);
    }

    protected int func_183019_a()
    {
        return 1;
    }

    protected String func_148311_a() {
        try {
            PackMetadataSection packMetadataSection = this.field_148320_d.getPackMetadata(
                    CommonResourceElement.Companion.getResourcePackRepository().rprMetadataSerializer, "pack");

            if (packMetadataSection != null) {
                return packMetadataSection.getPackDescription().getFormattedText();
            } else {
                logger.error("No metadata found in pack.mcmeta for this resource pack");
                logger.error("Expected pack.mcmeta to be in the same directory as the resource pack: " + field_148320_d.getPackName());
                return EnumChatFormatting.RED + "No metadata found in pack.mcmeta for this resource pack.";
            }
        } catch (JsonParseException jsonParseException) {
            logger.error("Couldn't parse pack metadata", jsonParseException);
            return EnumChatFormatting.RED + "Failed to parse pack metadata. Make sure pack.mcmeta is properly formatted.";
        } catch (IOException ioException) {
            logger.error("Couldn't load pack metadata", ioException);
            logger.error("Expected pack.mcmeta to be in the same directory as the resource pack: " + field_148320_d.getPackName());
            return EnumChatFormatting.RED + "Couldn't access pack.mcmeta. Ensure the file exists and is accessible.";
        }
    }

    protected boolean func_148309_e()
    {
        return false;
    }

    protected boolean func_148308_f()
    {
        return false;
    }

    protected boolean func_148314_g()
    {
        return false;
    }

    protected boolean func_148307_h()
    {
        return false;
    }

    protected String func_148312_b()
    {
        return "Default";
    }

    protected void func_148313_c()
    {
        this.mc.getTextureManager().bindTexture(this.resourcePackIcon);
    }

    protected boolean func_148310_d()
    {
        return false;
    }
}
