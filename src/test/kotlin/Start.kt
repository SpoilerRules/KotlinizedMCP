import net.minecraft.client.main.ClientInitializer

object Start {
    @JvmStatic
    fun main(args: Array<String>) {
        val defaultArgs = arrayOf(
            // Account-related
            "--username", "little_haxor",
            "--accessToken", "0",
            "--uuid", "41cdf1dc-19cd-460e-92d8-5e5dd13848ad",
            "--userProperties", "{}",

            // Version-related
            "--version", "Evanescent",
            "--assetIndex", "1.8",

            // Folder-related
            "--gameDir", System.getProperty("user.dir"),
            "--assetsDir", "assets"
        )
        ClientInitializer.main(args + defaultArgs)
    }
}