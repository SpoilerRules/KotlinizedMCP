package net.minecraft.client

import net.minecraft.client.resources.ResourcePackRepository

open class CommonResourceElement {
    protected lateinit var resourcePackRepository: ResourcePackRepository

    companion object {
        private val commonResourceElement: CommonResourceElement = CommonResourceElement()
        fun getResourcePackRepository(): ResourcePackRepository? =
            if (commonResourceElement::resourcePackRepository.isInitialized) commonResourceElement.resourcePackRepository else null
    }
}