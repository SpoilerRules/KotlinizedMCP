package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;

import java.util.List;

public class ServerSelectionList extends GuiListExtended
{
    private final GuiMultiplayer owner;
    private final List<ServerListEntryNormal> serverListInternet = Lists.<ServerListEntryNormal>newArrayList();
    private final List<ServerListEntryLanDetected> serverListLan = Lists.<ServerListEntryLanDetected>newArrayList();
    private final GuiListExtended.IGuiListEntry lanScanEntry = new ServerListEntryLanScan();
    private int selectedSlotIndex = -1;

    public ServerSelectionList(GuiMultiplayer ownerIn, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn)
    {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.owner = ownerIn;
    }

    public GuiListExtended.IGuiListEntry getListEntry(int index)
    {
        if (index < this.serverListInternet.size())
        {
            return (GuiListExtended.IGuiListEntry)this.serverListInternet.get(index);
        }
        else
        {
            index = index - this.serverListInternet.size();

            if (index == 0)
            {
                return this.lanScanEntry;
            }
            else
            {
                --index;
                return (GuiListExtended.IGuiListEntry)this.serverListLan.get(index);
            }
        }
    }

    protected int getSize()
    {
        return this.serverListInternet.size() + 1 + this.serverListLan.size();
    }

    public void setSelectedSlotIndex(int selectedSlotIndexIn)
    {
        this.selectedSlotIndex = selectedSlotIndexIn;
    }

    protected boolean isSelected(int slotIndex)
    {
        return slotIndex == this.selectedSlotIndex;
    }

    public int getSelectedIndex()
    {
        return this.selectedSlotIndex;
    }

    public void setServerList(ServerList updatedServerList) {
        this.serverListInternet.clear();

        for (int serverIndex = 0; serverIndex < updatedServerList.countServers(); ++serverIndex) {
            ServerData serverData = updatedServerList.getServerData(serverIndex);
            ServerListEntryNormal serverEntry = new ServerListEntryNormal(this.owner, serverData);
            this.serverListInternet.add(serverEntry);
        }
    }

    public void func_148194_a(List<LanServerDetector.LanServer> p_148194_1_)
    {
        this.serverListLan.clear();

        for (LanServerDetector.LanServer lanserverdetector$lanserver : p_148194_1_)
        {
            this.serverListLan.add(new ServerListEntryLanDetected(this.owner, lanserverdetector$lanserver));
        }
    }

    protected int getScrollBarX()
    {
        return super.getScrollBarX() + 30;
    }

    public int getListWidth()
    {
        return super.getListWidth() + 85;
    }
}
