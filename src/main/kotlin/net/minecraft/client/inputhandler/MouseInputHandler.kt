package net.minecraft.client.inputhandler

import net.minecraft.block.material.Material
import net.minecraft.client.settings.KeyBinding
import net.minecraft.util.MovingObjectPosition.MovingObjectType
import org.lwjgl.input.Mouse

class MouseInputHandler : InputService() {
    fun startHandlingMouseInput() {
        player ?: return

        if (activeScreen?.allowUserInput != false) {
            mc.mcProfiler.endStartSection("mouse")

            handleMouseClicks()
            while (Mouse.next()) handleMouseScroll()
        }
    }

    private fun handleMouseScroll() {
        val mouseButton = Mouse.getEventButton()
        val mouseButtonState = Mouse.getEventButtonState()

        player?.let { player ->
            KeyBinding.setKeyBindState(mouseButton - 100, mouseButtonState)

            if (mouseButtonState) {
                when {
                    player.isSpectator && mouseButton == 2 -> mc.ingameGUI.spectatorGui.func_175261_b()
                    else -> KeyBinding.onTick(mouseButton - 100)
                }
            }

            if (potentialTimeResolution <= 200L) {
                var mouseWheelDelta = Mouse.getEventDWheel()

                if (mouseWheelDelta != 0) {
                    when {
                        player.isSpectator -> {
                            mouseWheelDelta = if (mouseWheelDelta < 0) -1 else 1

                            if (mc.ingameGUI.spectatorGui.func_175262_a()) {
                                mc.ingameGUI.spectatorGui.func_175259_b(-mouseWheelDelta)
                            } else {
                                val flySpeedIncrement = mouseWheelDelta.toFloat() * 0.005f
                                player.capabilities?.let { capabilities ->
                                    val newFlySpeed = (capabilities.flySpeed + flySpeedIncrement).coerceIn(0.0f, 0.2f)
                                    capabilities.flySpeed = newFlySpeed
                                }
                            }
                        }
                        else -> player.inventory?.changeCurrentItem(mouseWheelDelta)
                    }
                }
            }
        }
    }

    private fun handleMouseClicks() {
        player?.let {
            if (!it.isUsingItem) {
                when {
                    mc.gameSettings.keyBindAttack.isPressed -> handleLeftClick()
                    mc.gameSettings.keyBindPickBlock.isPressed -> mc.middleClickMouse()
                    mc.gameSettings.keyBindUseItem.isKeyDown && mc.rightClickDelayTimer == 0 -> handleRightClick()
                }
            } else if (!mc.gameSettings.keyBindUseItem.isKeyDown) {
                mc.playerController.onStoppedUsingItem(it)
            }
        }

        mc.sendClickBlockToController(activeScreen == null && mc.gameSettings.keyBindAttack.isKeyDown && mc.inGameHasFocus)
    }

    /**
     * Executes a mouse click action in the game.
     *
     * This function is responsible for handling left mouse click actions within the game. It checks if the left click counter is less than or equal to zero, and if so, it performs a series of actions.
     *
     * The function first makes the player swing their item. Then, it checks the type of the object the mouse is currently over. If the mouse is over an entity, it makes the player attack that entity. If the mouse is over a block that is not air, it makes the player click on that block.
     *
     * If the mouse is not over any object, it logs a warning indicating an issue occurred while performing the left click action.
     *
     * @implNote This function does not implement the left click delay that was present in the 1.8 version of the game.
     *
     * @return void
     */
    private fun handleLeftClick() {
        mc.takeIf { it.leftClickCounter <= 0 }?.let {
            player?.swingItem()
            it.objectMouseOver?.let { targetedObject ->
                val typeOfHit = targetedObject.typeOfHit
                if (typeOfHit == MovingObjectType.ENTITY) {
                    it.playerController.attackEntity(player, targetedObject.entityHit)
                } else if (typeOfHit == MovingObjectType.BLOCK) {
                    val blockpos = targetedObject.blockPos
                    if (it.theWorld.getBlockState(blockpos).block.material != Material.air)
                        it.playerController.clickBlock(blockpos, targetedObject.sideHit)
                }
            } ?: run {
                logger.warn("An issue occurred while performing the left click action.")
            }
        }
    }

    /**
     * Handles right mouse click actions within the game.
     *
     * This function is invoked when a right mouse click action is performed in the game. It checks the object that the mouse is currently hovering over, which could be an entity or a block, and performs the appropriate actions based on the type of the object. If the mouse is not hovering over any object, it logs a warning message.
     *
     * The function first checks if the player is currently hitting a block. If so, it immediately returns without performing any action. Otherwise, it checks the type of the object that the mouse is hovering over. If the object is an entity, it checks if the player is right-clicking on the entity and if the player is sending an interaction packet to the entity. If either of these conditions is true, it sets a flag to not perform any further action.
     *
     * If the object is a block, it checks if the block is not air. If so, it performs a right-click action on the block and swings the player's hand. If the player's current item stack size becomes zero after the action, it removes the current item from the player's inventory. If the stack size of the current item changes after the action or if the player is in creative mode, it resets the progress of the item renderer.
     *
     * After handling the actions based on the type of the object, if the flag for performing action is still set and the player's current item stack size is not null, it sends a use item packet to the player and resets the progress of the item renderer.
     *
     * @return void
     */
    fun handleRightClick() {
        if (!mc.playerController.isHittingBlock) {
            mc.rightClickDelayTimer = 4
            var isActionPerformed = true
            val currentItem = currentItem

            when (mc.objectMouseOver?.typeOfHit) {
                MovingObjectType.ENTITY -> {
                    if (mc.playerController.isPlayerRightClickingOnEntity(player, mc.objectMouseOver.entityHit, mc.objectMouseOver) ||
                        mc.playerController.interactWithEntitySendPacket(player, mc.objectMouseOver.entityHit)) {
                        isActionPerformed = false
                    }
                }
                MovingObjectType.BLOCK -> {
                    val blockPos = mc.objectMouseOver.blockPos
                    if (!mc.theWorld.getBlockState(blockPos).block.material.equals(Material.air)) {
                        val initialStackSize = currentItem?.stackSize ?: 0

                        if (mc.playerController.onPlayerRightClick(player, mc.theWorld, currentItem, blockPos, mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec)) {
                            isActionPerformed = false
                            player?.swingItem()
                        }

                        currentItem?.let {
                            when {
                                it.stackSize == 0 -> player?.inventory!!.mainInventory[player.inventory!!.currentItem] = null
                                it.stackSize != initialStackSize -> mc.entityRenderer.itemRenderer.resetEquippedProgress()
                            }
                        }
                    }
                }

                else -> logger.warn("Null returned as 'hitResult', this shouldn't happen!")
            }

            if (isActionPerformed && currentItem != null && mc.playerController.sendUseItem(player, mc.theWorld, currentItem)) {
                mc.entityRenderer.itemRenderer.resetEquippedProgress2()
            }
        }
    }
    /*   private fun handleRightClick() {
        if (mc.playerController.isHittingBlock) return

        mc.objectMouseOver?.let { targetedObject ->
            mc.rightClickDelayTimer = 4

            val shouldPerformAction = when (targetedObject.typeOfHit) {
                MovingObjectPosition.MovingObjectType.ENTITY -> {
                    !mc.playerController.isPlayerRightClickingOnEntity(player, targetedObject.entityHit, targetedObject) &&
                            !mc.playerController.interactWithEntitySendPacket(player, targetedObject.entityHit)
                }

                MovingObjectPosition.MovingObjectType.BLOCK -> {
                    targetedObject.blockPos.let { blockPos ->
                        mc.theWorld.getBlockState(blockPos).block.material != Material.air || run {
                            val initialStackSize = currentItem?.stackSize

                            mc.playerController.onPlayerRightClick(
                                player,
                                mc.theWorld,
                                currentItem,
                                blockPos,
                                targetedObject.sideHit,
                                targetedObject.hitVec
                            ).also {
                                if (it) player?.swingItem()
                            }

                            currentItem?.apply {
                                if (stackSize == 0) {
                                    player?.inventory!!.mainInventory[player.inventory.currentItem] = null
                                } else if (stackSize != initialStackSize || mc.playerController.isInCreativeMode) {
                                    mc.entityRenderer.itemRenderer.resetEquippedProgress()
                                }
                            }
                            false
                        }
                    }
                }

                else -> true
            }

            if (shouldPerformAction && currentItem?.stackSize != null && mc.playerController.sendUseItem(
                    player,
                    mc.theWorld,
                    currentItem
                )
            ) {
                mc.entityRenderer.itemRenderer.resetEquippedProgress2()
            }
        } ?: logger.warn("An issue occurred while performing the right click action.")
    }

/*    @ExperimentalState
    private fun handleRightClick() {
        if (mc.playerController.isHittingBlock) return

        if (mc.objectMouseOver == null) {
            logger.warn("An issue occurred while performing the right click action.")
            return
        }

        mc.rightClickDelayTimer = 4
        var isActionPerformed = true

        val hitType = mc.objectMouseOver.typeOfHit

        if (hitType == MovingObjectType.ENTITY) {
            if (mc.playerController.isPlayerRightClickingOnEntity(player, mc.objectMouseOver.entityHit, mc.objectMouseOver) || mc.playerController.interactWithEntitySendPacket(player, mc.objectMouseOver.entityHit)) {
                isActionPerformed = false
            }
        } else if (hitType == MovingObjectType.BLOCK) {
            val blockPos: BlockPos = mc.objectMouseOver.blockPos
            val blockState = mc.theWorld.getBlockState(blockPos)

            if (blockState.block.material != Material.air) {
                val initialStackSize = currentItem?.stackSize ?: 0
                val success = mc.playerController.onPlayerRightClick(player, mc.theWorld, currentItem, blockPos, mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec)

                if (success) {
                    isActionPerformed = false
                    player?.swingItem()
                }

                currentItem?.let {
                    if (it.stackSize == 0) {
                        player?.inventory!!.mainInventory[player.inventory.currentItem] = null
                    } else if (it.stackSize != initialStackSize || mc.playerController.isInCreativeMode) {
                        mc.entityRenderer.itemRenderer.resetEquippedProgress()
                    }
                }
            }
        }

        player?.inventory?.getCurrentItem()?.let { currentItemProperty ->
            if (isActionPerformed && mc.playerController.sendUseItem(player, mc.theWorld, currentItemProperty)) {
                mc.entityRenderer.itemRenderer.resetEquippedProgress2()
            }
        }
    }*/

     */
}