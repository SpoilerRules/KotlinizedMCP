package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vector3D;
import net.minecraft.world.World;

public interface ICommandSender
{
    String getName();

    IChatComponent getDisplayName();

    void addChatMessage(IChatComponent component);

    boolean canCommandSenderUseCommand(int permLevel, String commandName);

    BlockPos getPosition();

    Vector3D getPositionVector();

    World getEntityWorld();

    Entity getCommandSenderEntity();

    boolean sendCommandFeedback();

    void setCommandStat(CommandResultStats.Type type, int amount);
}
