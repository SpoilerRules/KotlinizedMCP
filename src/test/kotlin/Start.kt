import net.minecraft.client.main.ClientInitializer

object Start {
    @JvmStatic
    fun main(args: Array<String>) {
        val additionalArgs = mapOf(
            // Account-related
            "--username" to "little_haxor",
            "--accessToken" to "0",
            "--uuid" to "41cdf1dc-19cd-460e-92d8-5e5dd13848ad",
            "--userProperties" to "{}",

            // Version-related
            "--version" to "Evanescent",
            "--assetIndex" to "1.8",

            // Folder-related
            "--gameDir" to System.getProperty("user.dir"),
            "--assetsDir" to "assets"
        ).flatMap { listOf(it.key, it.value) }.toTypedArray()

        ClientInitializer.main(args + additionalArgs)
    }
}