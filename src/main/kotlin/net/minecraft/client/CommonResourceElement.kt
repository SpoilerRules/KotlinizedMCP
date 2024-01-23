package net.minecraft.client

import net.minecraft.client.resources.ResourcePackRepository

data class CommonResourceElement(var resourcePackRepository: ResourcePackRepository? = null) {
    companion object {
        private val commonResourceElement = CommonResourceElement()

        var resourcePackRepository: ResourcePackRepository?
            get() = commonResourceElement.resourcePackRepository
            set(repository) { commonResourceElement.resourcePackRepository = repository }
    }
}