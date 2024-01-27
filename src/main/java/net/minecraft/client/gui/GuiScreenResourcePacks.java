package net.minecraft.client.gui;

import annotations.PendingRemoval;
import com.google.common.collect.Lists;
import net.minecraft.client.CommonResourceElement;
import net.minecraft.client.resources.*;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Sys;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@PendingRemoval //completely refactor this spaghetti in kotlin when the new resource pack system is being developed
public class GuiScreenResourcePacks extends GuiScreen {
    private static final Logger logger = LogManager.getLogger();
    private final GuiScreen parentScreen;
    private List<ResourcePackListEntry> availableResourcePacks;
    private List<ResourcePackListEntry> selectedResourcePacks;
    private GuiResourcePackAvailable availableResourcePacksList;
    private GuiResourcePackSelected selectedResourcePacksList;
    private boolean changed = false;

    public GuiScreenResourcePacks(GuiScreen parentScreenIn) {
        this.parentScreen = parentScreenIn;
    }

    public void initGui() {
        buttonList.add(new GuiOptionButton(2, this.width / 2 - 154, this.height - 48, LocalizationHelper.translate("resourcePack.openFolder")));
        buttonList.add(new GuiOptionButton(1, this.width / 2 + 4, this.height - 48, LocalizationHelper.translate("gui.done")));

        if (!this.changed) {
            this.availableResourcePacks = Lists.newArrayList();
            this.selectedResourcePacks = Lists.newArrayList();
            ResourcePackRepository resourcepackrepository = CommonResourceElement.Companion.getResourcePackRepository();
            CompletableFuture.runAsync(() -> resourcepackrepository.updateRepositoryEntriesAll());
            List<ResourcePackRepository.Entry> list = Lists.newArrayList(resourcepackrepository.getRepositoryEntriesAll());
            list.removeAll(resourcepackrepository.getRepositoryEntries());

            for (ResourcePackRepository.Entry resourcepackrepository$entry : list) {
                this.availableResourcePacks.add(new ResourcePackListEntryFound(this, resourcepackrepository$entry));
            }

            CompletableFuture.runAsync(() -> {
                for (ResourcePackRepository.Entry resourcepackrepository$entry1 : Lists.reverse(resourcepackrepository.getRepositoryEntries())) {
                    this.selectedResourcePacks.add(new ResourcePackListEntryFound(this, resourcepackrepository$entry1));
                }
            });

            this.selectedResourcePacks.add(new ResourcePackListEntryDefault(this));
        }

        this.availableResourcePacksList = new GuiResourcePackAvailable(this.mc, 200, this.height, this.availableResourcePacks);
        this.availableResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 - 4 - 200);
        this.availableResourcePacksList.registerScrollButtons(7, 8);
        this.selectedResourcePacksList = new GuiResourcePackSelected(this.mc, 200, this.height, this.selectedResourcePacks);
        this.selectedResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 + 4);
        this.selectedResourcePacksList.registerScrollButtons(7, 8);
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.selectedResourcePacksList.handleMouseInput();
        this.availableResourcePacksList.handleMouseInput();
    }

    public boolean hasResourcePackEntry(ResourcePackListEntry p_146961_1_) {
        return this.selectedResourcePacks.contains(p_146961_1_);
    }

    public List<ResourcePackListEntry> getListContaining(ResourcePackListEntry p_146962_1_) {
        return this.hasResourcePackEntry(p_146962_1_) ? this.selectedResourcePacks : this.availableResourcePacks;
    }

    public List<ResourcePackListEntry> getAvailableResourcePacks() {
        return this.availableResourcePacks;
    }

    public List<ResourcePackListEntry> getSelectedResourcePacks() {
        return this.selectedResourcePacks;
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 2) {
                File resourcePackDir = CommonResourceElement.Companion.getResourcePackRepository().getDirResourcepacks();
                String absolutePath = resourcePackDir.getAbsolutePath();

                if (Util.getOSType() == Util.EnumOS.OSX) {
                    try {
                        logger.info(absolutePath);
                        Runtime.getRuntime().exec(new String[]{"/usr/bin/open", absolutePath});
                        return;
                    } catch (IOException ioException) {
                        logger.error("Couldn't open file", ioException);
                    }
                } else if (Util.getOSType() == Util.EnumOS.WINDOWS) {
                    String command = String.format("cmd.exe /C start \"Open file\" \"%s\"", absolutePath);

                    try {
                        Process process = Runtime.getRuntime().exec(new String[]{command});
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }
                        reader.close();
                    } catch (IOException ioException) {
                        logger.error("Couldn't open file", ioException);
                    }
                }

                boolean hasError = false;

                try {
                    Class<?> desktopClass = Class.forName("java.awt.Desktop");
                    Object desktopInstance = desktopClass.getMethod("getDesktop").invoke(null);
                    desktopClass.getMethod("browse", URI.class).invoke(desktopInstance, resourcePackDir.toURI());
                } catch (Throwable throwable) {
                    logger.error("Couldn't open link", throwable);
                    hasError = true;
                }

                if (hasError) {
                    logger.info("Opening via system class!");
                    Sys.openURL("file://" + absolutePath);
                }
            } else if (button.id == 1) {
                if (changed) {
                    List<ResourcePackRepository.Entry> resourcePackEntries = Lists.newArrayList();

                    for (ResourcePackListEntry resourcePackListEntry : selectedResourcePacks) {
                        if (resourcePackListEntry instanceof ResourcePackListEntryFound) {
                            resourcePackEntries.add(((ResourcePackListEntryFound) resourcePackListEntry).func_148318_i());
                        }
                    }

                    Collections.reverse(resourcePackEntries);
                    CommonResourceElement.Companion.getResourcePackRepository().setRepositories(resourcePackEntries);
                    mc.gameSettings.resourcePacks.clear();
                    mc.gameSettings.incompatibleResourcePacks.clear();

                    for (ResourcePackRepository.Entry entry : resourcePackEntries) {
                        mc.gameSettings.resourcePacks.add(entry.getResourcePackName());

                        if (entry.func_183027_f() != 1) {
                            mc.gameSettings.incompatibleResourcePacks.add(entry.getResourcePackName());
                        }
                    }

                    mc.gameSettings.saveOptions();
                    mc.refreshResources();
                }

                mc.displayGuiScreen(parentScreen);
            }
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.availableResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
        this.selectedResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawBackground(0);
        this.availableResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
        this.selectedResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObject, LocalizationHelper.translate("resourcePack.title"), this.width / 2, 16, 16777215);
        this.drawCenteredString(this.fontRendererObject, LocalizationHelper.translate("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void markChanged() {
        this.changed = true;
    }
}
