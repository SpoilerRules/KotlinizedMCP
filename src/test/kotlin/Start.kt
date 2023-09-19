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
        val os = System.getProperty("os.name").lowercase()
        val osType = when {
            os.contains("win") -> "windows"
            os.contains("nix") || os.contains("nux") -> "linux"
            else -> throw UnsupportedOperationException("Unsupported operating system: $os")
        }
        System.setProperty("org.lwjgl.librarypath", findLibraryPath(osType))
    }

    private fun findLibraryPath(subdirectory: String) =
        File("../test_natives", subdirectory).takeIf { it.exists() && it.isDirectory }?.absolutePath?.replace('\\', '/')
            ?: throw IllegalStateException("Library path not found for OS: $subdirectory")
}