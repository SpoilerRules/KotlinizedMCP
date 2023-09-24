import java.io.File
import net.minecraft.client.main.PseudoMain as Main

object Start {
    @JvmStatic
    fun main(args: Array<String>) {
        setLwjglLibraryPath()
        val defaultArgs = arrayOf(
            "--username", "little_haxor",
            "--version", "Evanescent",
            "--assetsDir", "assets",
            "--assetIndex", "1.8",
            "--uuid", "41cdf1dc-19cd-460e-92d8-5e5dd13848ad",
            "--accessToken", "0",
            "--userProperties", "{}",
            "--userType", "offline"
        )
        Main(args + defaultArgs).main()
    }

    private fun setLwjglLibraryPath() {
        val osType = if (System.getProperty("os.name").startsWith("Windows")) "windows" else "linux"
        val rootDirectory = File("").canonicalFile.absolutePath.replace('\\', '/')
        System.setProperty("org.lwjgl.librarypath", "$rootDirectory/test_natives/$osType")
    }
}