package net.minecraft.client.multiplayer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.*;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public class PlayerControllerMP
{
    private final Minecraft mc;
    private final NetHandlerPlayClient netClientHandler;
    private BlockPos currentBlock = new BlockPos(-1, -1, -1);
    private ItemStack currentItemHittingBlock;
    private float curBlockDamageMP;
    private float stepSoundTickCounter;
    private int blockHitDelay;
    private boolean isHittingBlock;
    private WorldSettings.GameType currentGameType = WorldSettings.GameType.SURVIVAL;
    private int currentPlayerItem;

    public PlayerControllerMP(Minecraft mcIn, NetHandlerPlayClient netHandler)
    {
        this.mc = mcIn;
        this.netClientHandler = netHandler;
    }

    public static void clickBlockCreative(Minecraft mcIn, PlayerControllerMP playerController, BlockPos pos, EnumFacing facing)
    {
        if (!mcIn.theWorld.extinguishFire(mcIn.thePlayer, pos, facing))
        {
            playerController.onPlayerDestroyBlock(pos, facing);
        }
    }

    public void setPlayerCapabilities(EntityPlayer player)
    {
        this.currentGameType.configurePlayerCapabilities(player.capabilities);
    }

    public boolean isSpectator()
    {
        return this.currentGameType == WorldSettings.GameType.SPECTATOR;
    }

    public void setGameType(WorldSettings.GameType type)
    {
        this.currentGameType = type;
        this.currentGameType.configurePlayerCapabilities(this.mc.thePlayer.capabilities);
    }

    public void flipPlayer(EntityPlayer playerIn)
    {
        playerIn.rotationYaw = -180.0F;
    }

    public boolean shouldDrawHUD()
    {
        return this.currentGameType.isSurvivalOrAdventure();
    }

    public boolean onPlayerDestroyBlock(BlockPos pos, EnumFacing side)
    {
        if (this.currentGameType.isAdventure())
        {
            if (this.currentGameType == WorldSettings.GameType.SPECTATOR)
            {
                return false;
            }

            if (!this.mc.thePlayer.isAllowEdit())
            {
                Block block = this.mc.theWorld.getBlockState(pos).getBlock();
                ItemStack itemstack = this.mc.thePlayer.getCurrentEquippedItem();

                if (itemstack == null)
                {
                    return false;
                }

                if (!itemstack.canDestroy(block))
                {
                    return false;
                }
            }
        }

        if (this.currentGameType.isCreative() && this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)
        {
            return false;
        }
        else
        {
            World world = this.mc.theWorld;
            IBlockState iblockstate = world.getBlockState(pos);
            Block block1 = iblockstate.getBlock();

            if (block1.getMaterial() == Material.air)
            {
                return false;
            }
            else
            {
                world.playAuxSFX(2001, pos, Block.getStateId(iblockstate));
                boolean flag = world.setBlockToAir(pos);

                if (flag)
                {
                    block1.onBlockDestroyedByPlayer(world, pos, iblockstate);
                }

                this.currentBlock = new BlockPos(this.currentBlock.getX(), -1, this.currentBlock.getZ());

                if (!this.currentGameType.isCreative())
                {
                    ItemStack itemstack1 = this.mc.thePlayer.getCurrentEquippedItem();

                    if (itemstack1 != null)
                    {
                        itemstack1.onBlockDestroyed(world, block1, pos, this.mc.thePlayer);

                        if (itemstack1.stackSize == 0)
                        {
                            this.mc.thePlayer.destroyCurrentEquippedItem();
                        }
                    }
                }

                return flag;
            }
        }
    }

    public boolean clickBlock(BlockPos loc, EnumFacing face)
    {
        if (this.currentGameType.isAdventure())
        {
            if (this.currentGameType == WorldSettings.GameType.SPECTATOR)
            {
                return false;
            }

            if (!this.mc.thePlayer.isAllowEdit())
            {
                Block block = this.mc.theWorld.getBlockState(loc).getBlock();
                ItemStack itemstack = this.mc.thePlayer.getCurrentEquippedItem();

                if (itemstack == null)
                {
                    return false;
                }

                if (!itemstack.canDestroy(block))
                {
                    return false;
                }
            }
        }

        if (!this.mc.theWorld.getWorldBorder().contains(loc))
        {
            return false;
        }
        else
        {
            if (this.currentGameType.isCreative())
            {
                this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, loc, face));
                clickBlockCreative(this.mc, this, loc, face);
                this.blockHitDelay = 5;
            }
            else if (!this.isHittingBlock || !this.isHittingPosition(loc))
            {
                if (this.isHittingBlock)
                {
                    this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.currentBlock, face));
                }

                this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, loc, face));
                Block block1 = this.mc.theWorld.getBlockState(loc).getBlock();
                boolean flag = block1.getMaterial() != Material.air;

                if (flag && this.curBlockDamageMP == 0.0F)
                {
                    block1.onBlockClicked(this.mc.theWorld, loc, this.mc.thePlayer);
                }

                if (flag && block1.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, loc) >= 1.0F)
                {
                    this.onPlayerDestroyBlock(loc, face);
                }
                else
                {
                    this.isHittingBlock = true;
                    this.currentBlock = loc;
                    this.currentItemHittingBlock = this.mc.thePlayer.getHeldItem();
                    this.curBlockDamageMP = 0.0F;
                    this.stepSoundTickCounter = 0.0F;
                    this.mc.theWorld.sendBlockBreakProgress(this.mc.thePlayer.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
                }
            }

            return true;
        }
    }

    public void resetBlockRemoving()
    {
        if (this.isHittingBlock)
        {
            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.currentBlock, EnumFacing.DOWN));
            this.isHittingBlock = false;
            this.curBlockDamageMP = 0.0F;
            this.mc.theWorld.sendBlockBreakProgress(this.mc.thePlayer.getEntityId(), this.currentBlock, -1);
        }
    }

    public boolean onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing)
    {
        this.syncCurrentPlayItem();

        if (this.blockHitDelay > 0)
        {
            --this.blockHitDelay;
            return true;
        }
        else if (this.currentGameType.isCreative() && this.mc.theWorld.getWorldBorder().contains(posBlock))
        {
            this.blockHitDelay = 5;
            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, posBlock, directionFacing));
            clickBlockCreative(this.mc, this, posBlock, directionFacing);
            return true;
        }
        else if (this.isHittingPosition(posBlock))
        {
            Block block = this.mc.theWorld.getBlockState(posBlock).getBlock();

            if (block.getMaterial() == Material.air)
            {
                this.isHittingBlock = false;
                return false;
            }
            else
            {
                this.curBlockDamageMP += block.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, posBlock);

                if (this.stepSoundTickCounter % 4.0F == 0.0F)
                {
                    this.mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation(block.stepSound.getStepSound()), (block.stepSound.getVolume() + 1.0F) / 8.0F, block.stepSound.getFrequency() * 0.5F, (float)posBlock.getX() + 0.5F, (float)posBlock.getY() + 0.5F, (float)posBlock.getZ() + 0.5F));
                }

                ++this.stepSoundTickCounter;

                if (this.curBlockDamageMP >= 1.0F)
                {
                    this.isHittingBlock = false;
                    this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, posBlock, directionFacing));
                    this.onPlayerDestroyBlock(posBlock, directionFacing);
                    this.curBlockDamageMP = 0.0F;
                    this.stepSoundTickCounter = 0.0F;
                    this.blockHitDelay = 5;
                }

                this.mc.theWorld.sendBlockBreakProgress(this.mc.thePlayer.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
                return true;
            }
        }
        else
        {
            return this.clickBlock(posBlock, directionFacing);
        }
    }

    public float getBlockReachDistance()
    {
        return this.currentGameType.isCreative() ? 5.0F : 4.5F;
    }

    public void updateController()
    {
        this.syncCurrentPlayItem();

        if (this.netClientHandler.getNetworkManager().isChannelOpen())
        {
            this.netClientHandler.getNetworkManager().processReceivedPackets();
        }
        else
        {
            this.netClientHandler.getNetworkManager().checkDisconnected();
        }
    }

    private boolean isHittingPosition(BlockPos pos)
    {
        ItemStack itemstack = this.mc.thePlayer.getHeldItem();
        boolean flag = this.currentItemHittingBlock == null && itemstack == null;

        if (this.currentItemHittingBlock != null && itemstack != null)
        {
            flag = itemstack.getItem() == this.currentItemHittingBlock.getItem() && ItemStack.areItemStackTagsEqual(itemstack, this.currentItemHittingBlock) && (itemstack.isItemStackDamageable() || itemstack.getMetadata() == this.currentItemHittingBlock.getMetadata());
        }

        return pos.equals(this.currentBlock) && flag;
    }

    private void syncCurrentPlayItem()
    {
        int i = this.mc.thePlayer.inventory.currentItem;

        if (i != this.currentPlayerItem)
        {
            this.currentPlayerItem = i;
            this.netClientHandler.addToSendQueue(new C09PacketHeldItemChange(this.currentPlayerItem));
        }
    }

  /*  public boolean onPlayerRightClick(EntityPlayerSP player, WorldClient world, ItemStack heldItem, BlockPos position, EnumFacing side, Vector3D hitVector) {
        this.syncCurrentPlayItem();
        float hitVecX = (float) (hitVector.x - (double) position.getX());
        float hitVecY = (float) (hitVector.y - (double) position.getY());
        float hitVecZ = (float) (hitVector.z - (double) position.getZ());
        boolean isBlockActivated = false;

        if (!this.mc.theWorld.getWorldBorder().contains(position)) {
            return false;
        } else {
            if (this.currentGameType != WorldSettings.GameType.SPECTATOR) {
                IBlockState blockState = world.getBlockState(position);

                if ((!player.isSneaking() || player.getHeldItem() == null) && blockState.getBlock().onBlockActivated(world, position, blockState, player, side, hitVecX, hitVecY, hitVecZ)) {
                    isBlockActivated = true;
                }

                if (!isBlockActivated && heldItem != null && heldItem.getItem() instanceof ItemBlock itemBlock) {
                    if (!itemBlock.canPlaceBlockOnSide(world, position, side, player, heldItem)) {
                        return false;
                    }
                }
            }

            this.netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(position, side.getIndex(), player.inventory.getCurrentItem(), hitVecX, hitVecY, hitVecZ));

            if (!isBlockActivated && this.currentGameType != WorldSettings.GameType.SPECTATOR) {
                if (heldItem == null) {
                    return false;
                } else if (this.currentGameType.isCreative()) {
                    int metadata = heldItem.getMetadata();
                    int stackSize = heldItem.stackSize;
                    boolean isItemUsed = heldItem.onItemUse(player, world, position, side, hitVecX, hitVecY, hitVecZ);
                    heldItem.setItemDamage(metadata);
                    heldItem.stackSize = stackSize;
                    return isItemUsed;
                } else {
                    return heldItem.onItemUse(player, world, position, side, hitVecX, hitVecY, hitVecZ);
                }
            } else {
                return true;
            }
        }
    }*/
    public boolean onPlayerRightClick(EntityPlayerSP player, WorldClient world, ItemStack heldItem, BlockPos position, EnumFacing side, Vector3D hitVector) {
        this.syncCurrentPlayItem();
        float hitVecX = (float) (hitVector.x - (double) position.getX());
        float hitVecY = (float) (hitVector.y - (double) position.getY());
        float hitVecZ = (float) (hitVector.z - (double) position.getZ());

        if (!this.mc.theWorld.getWorldBorder().contains(position)) {
            return false;
        }

        if (this.currentGameType == WorldSettings.GameType.SPECTATOR) {
            return true;
        }

        IBlockState blockState = world.getBlockState(position);
        boolean isBlockActivated = (!player.isSneaking() || player.getHeldItem() == null) && blockState.getBlock().onBlockActivated(world, position, blockState, player, side, hitVecX, hitVecY, hitVecZ);

        if (!isBlockActivated && heldItem != null && heldItem.getItem() instanceof ItemBlock itemBlock && !itemBlock.canPlaceBlockOnSide(world, position, side, player, heldItem)) {
            return false;
        }

        this.netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(position, side.getIndex(), player.inventory.getCurrentItem(), hitVecX, hitVecY, hitVecZ));

        if (heldItem == null) {
            return false;
        }

        if (this.currentGameType.isCreative()) {
            int metadata = heldItem.getMetadata();
            int stackSize = heldItem.stackSize;
            boolean isItemUsed = heldItem.onItemUse(player, world, position, side, hitVecX, hitVecY, hitVecZ);
            heldItem.setItemDamage(metadata);
            heldItem.stackSize = stackSize;
            return isItemUsed;
        }

        return heldItem.onItemUse(player, world, position, side, hitVecX, hitVecY, hitVecZ);
    }

    public boolean sendUseItem(EntityPlayer playerIn, World worldIn, ItemStack itemStackIn) {
        if (this.currentGameType == WorldSettings.GameType.SPECTATOR) {
            return false;
        } else {
            this.syncCurrentPlayItem();
            this.netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(playerIn.inventory.getCurrentItem()));
            int i = itemStackIn.stackSize;
            ItemStack itemstack = itemStackIn.useItemRightClick(worldIn, playerIn);

            if (itemstack != itemStackIn || itemstack != null && itemstack.stackSize != i) {
                playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = itemstack;

                if (itemstack.stackSize == 0) {
                    playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
                }

                return true;
            } else {
                return false;
            }
        }
    }

    public EntityPlayerSP func_178892_a(World worldIn, StatFileWriter statWriter)
    {
        return new EntityPlayerSP(this.mc, worldIn, this.netClientHandler, statWriter);
    }

    public void attackEntity(EntityPlayer playerIn, Entity targetEntity)
    {
        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new C02PacketUseEntity(targetEntity, C02PacketUseEntity.Action.ATTACK));

        if (this.currentGameType != WorldSettings.GameType.SPECTATOR)
        {
            playerIn.attackTargetEntityWithCurrentItem(targetEntity);
        }
    }

    public boolean interactWithEntitySendPacket(EntityPlayer playerIn, Entity targetEntity)
    {
        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new C02PacketUseEntity(targetEntity, C02PacketUseEntity.Action.INTERACT));
        return this.currentGameType != WorldSettings.GameType.SPECTATOR && playerIn.interactWith(targetEntity);
    }

    public boolean isPlayerRightClickingOnEntity(EntityPlayer player, Entity entityIn, MovingObjectPosition movingObject)
    {
        this.syncCurrentPlayItem();
        Vector3D vector3D = new Vector3D(movingObject.hitVec.x - entityIn.posX, movingObject.hitVec.y - entityIn.posY, movingObject.hitVec.z - entityIn.posZ);
        this.netClientHandler.addToSendQueue(new C02PacketUseEntity(entityIn, vector3D));
        return this.currentGameType != WorldSettings.GameType.SPECTATOR && entityIn.interactAt(player, vector3D);
    }

    public ItemStack windowClick(int windowId, int slotId, int mouseButtonClicked, int mode, EntityPlayer playerIn)
    {
        short short1 = playerIn.openContainer.getNextTransactionID(playerIn.inventory);
        ItemStack itemstack = playerIn.openContainer.slotClick(slotId, mouseButtonClicked, mode, playerIn);
        this.netClientHandler.addToSendQueue(new C0EPacketClickWindow(windowId, slotId, mouseButtonClicked, mode, itemstack, short1));
        return itemstack;
    }

    public void sendEnchantPacket(int windowID, int button)
    {
        this.netClientHandler.addToSendQueue(new C11PacketEnchantItem(windowID, button));
    }

    public void sendSlotPacket(ItemStack itemStackIn, int slotId)
    {
        if (this.currentGameType.isCreative())
        {
            this.netClientHandler.addToSendQueue(new C10PacketCreativeInventoryAction(slotId, itemStackIn));
        }
    }

    public void sendPacketDropItem(ItemStack itemStackIn)
    {
        if (this.currentGameType.isCreative() && itemStackIn != null)
        {
            this.netClientHandler.addToSendQueue(new C10PacketCreativeInventoryAction(-1, itemStackIn));
        }
    }

    public void onStoppedUsingItem(EntityPlayer playerIn)
    {
        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        playerIn.stopUsingItem();
    }

    public boolean gameIsSurvivalOrAdventure()
    {
        return this.currentGameType.isSurvivalOrAdventure();
    }

    public boolean isNotCreative()
    {
        return !this.currentGameType.isCreative();
    }

    public boolean isInCreativeMode()
    {
        return this.currentGameType.isCreative();
    }

    public boolean isRidingHorse()
    {
        return this.mc.thePlayer.isRiding() && this.mc.thePlayer.ridingEntity instanceof EntityHorse;
    }

    public boolean isSpectatorMode()
    {
        return this.currentGameType == WorldSettings.GameType.SPECTATOR;
    }

    public WorldSettings.GameType getCurrentGameType()
    {
        return this.currentGameType;
    }

    public boolean getIsHittingBlock()
    {
        return this.isHittingBlock;
    }
}
