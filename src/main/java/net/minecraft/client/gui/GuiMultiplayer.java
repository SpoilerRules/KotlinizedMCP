package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.network.ServerPingHandlerService;
import net.minecraft.client.resources.LocalizationHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.List;

public class GuiMultiplayer extends GuiScreen implements GuiYesNoCallback {
    private static final Logger logger = LogManager.getLogger();
    private final ServerPingHandlerService serverPingHandlerService = new ServerPingHandlerService();
    private final GuiScreen parentScreen;
    private ServerSelectionList serverListSelector;
    private ServerList savedServerList;
    private GuiButton btnEditServer;
    private GuiButton btnSelectServer;
    private GuiButton btnDeleteServer;
    private boolean deletingServer;
    private boolean addingServer;
    private boolean editingServer;
    private boolean directConnect;
    private String hoveringText;
    private ServerData selectedServer;
    private LanServerDetector.LanServerList lanServerList;
    private LanServerDetector.ThreadLanServerFind lanServerDetector;
    private boolean initialized;

    public GuiMultiplayer(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        buttonList.clear();

        if (!this.initialized) {
            this.initialized = true;
            this.savedServerList = new ServerList(this.mc);
            this.savedServerList.loadServerList();
            this.lanServerList = new LanServerDetector.LanServerList();

            try {
                this.lanServerDetector = new LanServerDetector.ThreadLanServerFind(this.lanServerList);
                this.lanServerDetector.start();
            } catch (Exception exception) {
                logger.warn("Unable to start LAN server detection: " + exception.getMessage());
            }

            this.serverListSelector = new ServerSelectionList(this, this.mc, this.width, this.height, 32, this.height - 64, 36);
            this.serverListSelector.setServerList(this.savedServerList);
        } else {
            this.serverListSelector.setDimensions(this.width, this.height, 32, this.height - 64);
        }

        this.createButtons();
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.serverListSelector.handleMouseInput();
    }

    public void createButtons() {
        buttonList.add(this.btnEditServer = new GuiButton(7, this.width / 2 - 154, this.height - 28, 70, 20, LocalizationHelper.translate("selectServer.edit")));
        buttonList.add(this.btnDeleteServer = new GuiButton(2, this.width / 2 - 74, this.height - 28, 70, 20, LocalizationHelper.translate("selectServer.delete")));
        buttonList.add(this.btnSelectServer = new GuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, LocalizationHelper.translate("selectServer.select")));
        buttonList.add(new GuiButton(4, this.width / 2 - 50, this.height - 52, 100, 20, LocalizationHelper.translate("selectServer.direct")));
        buttonList.add(new GuiButton(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, LocalizationHelper.translate("selectServer.add")));
        buttonList.add(new GuiButton(8, this.width / 2 + 4, this.height - 28, 70, 20, LocalizationHelper.translate("selectServer.refresh")));
        buttonList.add(new GuiButton(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, LocalizationHelper.translate("gui.cancel")));
        this.selectServer(this.serverListSelector.getSelectedIndex());
    }

    public void updateScreen() {
        super.updateScreen();

        if (this.lanServerList.getWasUpdated()) {
            List<LanServerDetector.LanServer> list = this.lanServerList.getLanServers();
            this.lanServerList.setWasNotUpdated();
            this.serverListSelector.func_148194_a(list);
        }

        this.serverPingHandlerService.pingPendingNetworks();
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);

        if (this.lanServerDetector != null) {
            this.lanServerDetector.interrupt();
            this.lanServerDetector = null;
        }

        this.serverPingHandlerService.clearPendingNetworks();
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            GuiListExtended.IGuiListEntry guilistextended$iguilistentry = this.serverListSelector.getSelectedIndex() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelectedIndex());

            if (button.id == 2 && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                String s4 = ((ServerListEntryNormal) guilistextended$iguilistentry).getServerData().serverName;

                if (s4 != null) {
                    this.deletingServer = true;
                    String s = LocalizationHelper.translate("selectServer.deleteQuestion");
                    String s1 = "'" + s4 + "' " + LocalizationHelper.translate("selectServer.deleteWarning");
                    String s2 = LocalizationHelper.translate("selectServer.deleteButton");
                    String s3 = LocalizationHelper.translate("gui.cancel");
                    GuiYesNo guiyesno = new GuiYesNo(this, s, s1, s2, s3, this.serverListSelector.getSelectedIndex());
                    this.mc.displayGuiScreen(guiyesno);
                }
            } else if (button.id == 1) {
                this.connectToSelected();
            } else if (button.id == 4) {
                this.directConnect = true;
                this.mc.displayGuiScreen(new GuiScreenServerList(this, this.selectedServer = new ServerData(LocalizationHelper.translate("selectServer.defaultName"), "", false)));
            } else if (button.id == 3) {
                this.addingServer = true;
                this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.selectedServer = new ServerData(LocalizationHelper.translate("selectServer.defaultName"), "", false)));
            } else if (button.id == 7 && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                this.editingServer = true;
                ServerData serverdata = ((ServerListEntryNormal) guilistextended$iguilistentry).getServerData();
                this.selectedServer = new ServerData(serverdata.serverName, serverdata.serverIP, false);
                this.selectedServer.copyFrom(serverdata);
                this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.selectedServer));
            } else if (button.id == 0) {
                this.mc.displayGuiScreen(this.parentScreen);
            } else if (button.id == 8) {
                this.refreshServerList();
            }
        }
    }

    private void refreshServerList() {
        this.mc.displayGuiScreen(new GuiMultiplayer(this.parentScreen));
    }

    public void confirmClicked(boolean result, int id) {
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry = this.serverListSelector.getSelectedIndex() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelectedIndex());

        if (this.deletingServer) {
            this.deletingServer = false;

            if (result && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                this.savedServerList.removeServerData(this.serverListSelector.getSelectedIndex());
                this.savedServerList.saveServerList();
                this.serverListSelector.setSelectedSlotIndex(-1);
                this.serverListSelector.setServerList(this.savedServerList);
            }

            this.mc.displayGuiScreen(this);
        } else if (this.directConnect) {
            this.directConnect = false;

            if (result) {
                this.connectToServer(this.selectedServer);
            } else {
                this.mc.displayGuiScreen(this);
            }
        } else if (this.addingServer) {
            this.addingServer = false;

            if (result) {
                this.savedServerList.addServerData(this.selectedServer);
                this.savedServerList.saveServerList();
                this.serverListSelector.setSelectedSlotIndex(-1);
                this.serverListSelector.setServerList(this.savedServerList);
            }

            this.mc.displayGuiScreen(this);
        } else if (this.editingServer) {
            this.editingServer = false;

            if (result && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                ServerData serverdata = ((ServerListEntryNormal) guilistextended$iguilistentry).getServerData();
                serverdata.serverName = this.selectedServer.serverName;
                serverdata.serverIP = this.selectedServer.serverIP;
                serverdata.copyFrom(this.selectedServer);
                this.savedServerList.saveServerList();
                this.serverListSelector.setServerList(this.savedServerList);
            }

            this.mc.displayGuiScreen(this);
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        int i = this.serverListSelector.getSelectedIndex();
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry = i < 0 ? null : this.serverListSelector.getListEntry(i);

        if (keyCode == 63) {
            this.refreshServerList();
        } else {
            if (i >= 0) {
                if (keyCode == 200) {
                    if (isShiftKeyDown()) {
                        if (i > 0 && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                            this.savedServerList.swapServers(i, i - 1);
                            this.selectServer(this.serverListSelector.getSelectedIndex() - 1);
                            this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                            this.serverListSelector.setServerList(this.savedServerList);
                        }
                    } else if (i > 0) {
                        this.selectServer(this.serverListSelector.getSelectedIndex() - 1);
                        this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());

                        if (this.serverListSelector.getListEntry(this.serverListSelector.getSelectedIndex()) instanceof ServerListEntryLanScan) {
                            if (this.serverListSelector.getSelectedIndex() > 0) {
                                this.selectServer(this.serverListSelector.getSize() - 1);
                                this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                            } else {
                                this.selectServer(-1);
                            }
                        }
                    } else {
                        this.selectServer(-1);
                    }
                } else if (keyCode == 208) {
                    if (isShiftKeyDown()) {
                        if (i < this.savedServerList.countServers() - 1) {
                            this.savedServerList.swapServers(i, i + 1);
                            this.selectServer(i + 1);
                            this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                            this.serverListSelector.setServerList(this.savedServerList);
                        }
                    } else if (i < this.serverListSelector.getSize()) {
                        this.selectServer(this.serverListSelector.getSelectedIndex() + 1);
                        this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());

                        if (this.serverListSelector.getListEntry(this.serverListSelector.getSelectedIndex()) instanceof ServerListEntryLanScan) {
                            if (this.serverListSelector.getSelectedIndex() < this.serverListSelector.getSize() - 1) {
                                this.selectServer(this.serverListSelector.getSize() + 1);
                                this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                            } else {
                                this.selectServer(-1);
                            }
                        }
                    } else {
                        this.selectServer(-1);
                    }
                } else if (keyCode != 28 && keyCode != 156) {
                    super.keyTyped(typedChar, keyCode);
                } else {
                    this.actionPerformed(buttonList.get(2));
                }
            } else {
                super.keyTyped(typedChar, keyCode);
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.hoveringText = null;
        this.drawDefaultBackground();
        this.serverListSelector.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObject, LocalizationHelper.translate("multiplayer.title"), this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.hoveringText != null) {
            this.drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.hoveringText)), mouseX, mouseY);
        }
    }

    public void connectToSelected() {
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry = this.serverListSelector.getSelectedIndex() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelectedIndex());

        if (guilistextended$iguilistentry instanceof ServerListEntryNormal) {
            this.connectToServer(((ServerListEntryNormal) guilistextended$iguilistentry).getServerData());
        } else if (guilistextended$iguilistentry instanceof ServerListEntryLanDetected) {
            LanServerDetector.LanServer lanserverdetector$lanserver = ((ServerListEntryLanDetected) guilistextended$iguilistentry).getLanServer();
            this.connectToServer(new ServerData(lanserverdetector$lanserver.getServerMotd(), lanserverdetector$lanserver.getServerIpPort(), true));
        }
    }

    private void connectToServer(ServerData server) {
        this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, server));
    }

    public void selectServer(int index) {
        this.serverListSelector.setSelectedSlotIndex(index);
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry = index < 0 ? null : this.serverListSelector.getListEntry(index);
        this.btnSelectServer.enabled = false;
        this.btnEditServer.enabled = false;
        this.btnDeleteServer.enabled = false;

        if (guilistextended$iguilistentry != null && !(guilistextended$iguilistentry instanceof ServerListEntryLanScan)) {
            this.btnSelectServer.enabled = true;

            if (guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                this.btnEditServer.enabled = true;
                this.btnDeleteServer.enabled = true;
            }
        }
    }

    public ServerPingHandlerService getOldServerPinger() {
        return this.serverPingHandlerService;
    }

    public void setHoveringText(String p_146793_1_) {
        this.hoveringText = p_146793_1_;
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.serverListSelector.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.serverListSelector.mouseReleased(mouseX, mouseY, state);
    }

    public ServerList getServerList() {
        return this.savedServerList;
    }

    public boolean canEditServer(int p_175392_2_) {
        return p_175392_2_ > 0;
    }

    public boolean canDeleteServer(int p_175394_2_) {
        return p_175394_2_ < this.savedServerList.countServers() - 1;
    }

    public void editServer(int serverIndexToEdit, boolean isShiftKeyDown) {
        int targetIndex = isShiftKeyDown ? 0 : serverIndexToEdit - 1;

        this.savedServerList.swapServers(serverIndexToEdit, targetIndex);

        if (this.serverListSelector.getSelectedIndex() == serverIndexToEdit) {
            this.selectServer(targetIndex);
        }

        this.serverListSelector.setServerList(this.savedServerList);
    }

    public void deleteServer(int serverIndexToDelete, boolean isShiftKeyDown) {
        int targetIndex = isShiftKeyDown ? this.savedServerList.countServers() - 1 : serverIndexToDelete + 1;

        this.savedServerList.swapServers(serverIndexToDelete, targetIndex);

        if (this.serverListSelector.getSelectedIndex() == serverIndexToDelete) {
            this.selectServer(targetIndex);
        }

        this.serverListSelector.setServerList(this.savedServerList);
    }
}
