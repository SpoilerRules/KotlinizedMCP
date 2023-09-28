package net.minecraft.inventory

import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.BlockPos
import net.minecraft.world.World
import kotlin.random.Random
import kotlin.random.asJavaRandom

/**
 * Helper class for handling inventory related operations.
 */
object InventoryHelper {
    /**
     * Drops all items from the inventory at the specified position.
     */
    @JvmStatic
    fun dropInventoryItemsAtPosition(worldIn: World, pos: BlockPos, inventory: IInventory) =
        dropInventoryItems(worldIn, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), inventory)

    /**
     * Drops all items from the inventory at the entity's position.
     */
    @JvmStatic
    fun dropInventoryItemsAtEntity(worldIn: World, entityAt: Entity, inventory: IInventory) =
        dropInventoryItems(worldIn, entityAt.posX, entityAt.posY, entityAt.posZ, inventory)

    private fun dropInventoryItems(world: World, x: Double, y: Double, z: Double, inventory: IInventory) {
        for (slotIndex in 0 until inventory.sizeInventory) {
            val itemStack = inventory.getStackInSlot(slotIndex)

            itemStack?.let {
                spawnItemStack(world, x, y, z, it)
            }
        }
    }

    private fun spawnItemStack(world: World, x: Double, y: Double, z: Double, stack: ItemStack) {
        val nextGaussian = { Random.asJavaRandom().nextGaussian() }
        var remaining = stack.stackSize

        while (remaining > 0) {
            val amountToDrop = minOf(Random.nextInt(21) + 10, remaining)

            remaining -= amountToDrop

            val entityItem = EntityItem(
                world,
                x + Random.nextFloat() * 0.8F + 0.1F,
                y + Random.nextFloat() * 0.8F + 0.1F,
                z + Random.nextFloat() * 0.8F + 0.1F,
                ItemStack(stack.item, amountToDrop, stack.metadata)
            )

            if (stack.hasTagCompound()) {
                entityItem.entityItem.tagCompound = stack.tagCompound.copy() as NBTTagCompound
            }

            val motionFactor = 0.05F
            entityItem.apply {
                motionX = nextGaussian() * motionFactor
                motionY = nextGaussian() * motionFactor + 0.20000000298023224
                motionZ = nextGaussian() * motionFactor
            }
            world.spawnEntityInWorld(entityItem)
        }
    }
}