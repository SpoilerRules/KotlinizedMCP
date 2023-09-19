package net.minecraft.client.resources

import java.io.File
import java.io.IOException
import java.util.zip.ZipFile

class FileResourcePack(resourcePackFileIn: File) : AbstractResourcePack(resourcePackFileIn), AutoCloseable {
    private val resourcePackZipFile: ZipFile by lazy { ZipFile(resourcePackFile) }

    override fun getInputStreamByName(name: String) =
        resourcePackZipFile.getEntry(name)?.let { resourcePackZipFile.getInputStream(it) }
            ?: throw ResourcePackFileNotFoundException(resourcePackFile, name)

    override fun hasResourceName(name: String) = resourcePackZipFile.getEntry(name) != null

    override fun getResourceDomains() = try {
        resourcePackZipFile.entries().asSequence()
            .filter { it.name.startsWith("assets/") }
            .map { it.name.split("/").drop(1) }
            .filter { it.size > 1 }
            .map { it[0] }
            .filter { it == it.lowercase() }
            .toSet()
    } catch (e: IOException) {
        emptySet()
    }

    override fun close() {
        resourcePackZipFile.close()
    }
}