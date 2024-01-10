package net.minecraft.client

import net.minecraft.client.resources.ResourcePackRepository

data class CommonResourceElement(var resourcePackRepository: ResourcePackRepository? = null) {
    companion object {
        private val commonResourceElement: CommonResourceElement = CommonResourceElement()

        fun getResourcePackRepository(): ResourcePackRepository? =
            commonResourceElement.resourcePackRepository

        fun setResourcePackRepository(repository: ResourcePackRepository) {
            commonResourceElement.resourcePackRepository = repository
        }
    }
}