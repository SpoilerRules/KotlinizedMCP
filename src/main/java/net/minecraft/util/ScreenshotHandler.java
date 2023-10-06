package net.minecraft.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

// Pending kotlinization.
public class ScreenshotHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    private static IntBuffer pixelBuffer;
    private static int[] pixelValues;

    public static IChatComponent takeScreenshot(File gameDirectory, int width, int height, Framebuffer framebuffer) {
        try {
            File screenshotDir = createScreenshotDirectory(gameDirectory);

            if (OpenGlHelper.isFramebufferEnabled()) {
                width = framebuffer.framebufferTextureWidth;
                height = framebuffer.framebufferTextureHeight;
            }

            int pixelCount = width * height;
            initPixelBuffer(pixelCount);
            readPixelsIntoBuffer(width, height, framebuffer);

            BufferedImage screenshotImage = createScreenshotImage(width, height, framebuffer);

            File screenshotFile = getTimestampedPNGFileForDirectory(screenshotDir);

            saveScreenshotImage(screenshotImage, screenshotFile);

            IChatComponent chatComponent = createChatComponent(screenshotFile);

            return new ChatComponentTranslation("screenshot.success", chatComponent);
        } catch (Exception exception) {
            LOGGER.warn("Couldn't save screenshot", exception);
            return new ChatComponentTranslation("screenshot.failure", exception.getMessage());
        }
    }

    private static File createScreenshotDirectory(File gameDirectory) throws IOException {
        File screenshotDir = new File(gameDirectory, "screenshots");
        if (!screenshotDir.exists() && !screenshotDir.mkdir()) {
            throw new IOException("Failed to create screenshot directory.");
        }
        return screenshotDir;
    }

    private static void initPixelBuffer(int pixelCount) {
        if (pixelBuffer == null || pixelBuffer.capacity() < pixelCount) {
            pixelBuffer = BufferUtils.createIntBuffer(pixelCount);
            pixelValues = new int[pixelCount];
        }
    }

    private static void readPixelsIntoBuffer(int width, int height, Framebuffer framebuffer) {
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        pixelBuffer.clear();

        if (OpenGlHelper.isFramebufferEnabled()) {
            GlStateManager.bindTexture(framebuffer.framebufferTexture);
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        } else {
            GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        }

        pixelBuffer.get(pixelValues);
        TextureUtil.processPixelValues(pixelValues, width, height);
    }

    private static BufferedImage createScreenshotImage(int width, int height, Framebuffer framebuffer) {
        BufferedImage screenshotImage;

        if (OpenGlHelper.isFramebufferEnabled()) {
            screenshotImage = new BufferedImage(framebuffer.framebufferWidth, framebuffer.framebufferHeight, 1);
            int yOffset = framebuffer.framebufferTextureHeight - framebuffer.framebufferHeight;

            for (int y = yOffset; y < framebuffer.framebufferTextureHeight; ++y) {
                for (int x = 0; x < framebuffer.framebufferWidth; ++x) {
                    screenshotImage.setRGB(x, y - yOffset, pixelValues[y * framebuffer.framebufferTextureWidth + x]);
                }
            }
        } else {
            screenshotImage = new BufferedImage(width, height, 1);
            screenshotImage.setRGB(0, 0, width, height, pixelValues, 0, width);
        }

        return screenshotImage;
    }

    private static File getTimestampedPNGFileForDirectory(File screenshotDir) {
        String timestamp = DATE_FORMAT.format(new Date());
        String fileName;
        File screenshotFile;
        for (int suffix = 1; ; suffix++) {
            fileName = timestamp + (suffix == 1 ? "" : "_" + suffix) + ".png";
            screenshotFile = new File(screenshotDir, fileName);
            if (!screenshotFile.exists()) break;
        }
        return screenshotFile;
    }

    private static void saveScreenshotImage(BufferedImage screenshotImage, File screenshotFile) throws IOException {
        ImageIO.write(screenshotImage, "png", screenshotFile);
    }

    private static IChatComponent createChatComponent(File screenshotFile) {
        IChatComponent chatComponent = new ChatComponentText(screenshotFile.getName());
        chatComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, screenshotFile.getAbsolutePath()));
        chatComponent.getChatStyle().setUnderlined(true);
        return chatComponent;
    }
}