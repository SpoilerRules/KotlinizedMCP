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
        // Please set the path below to point to the correct location of 'lwjgl64.dll' on your system,
        // using forward slashes ('/') for the directory separators and choosing the appropriate path
        // for your operating system ("windows" or "linux").
        //                             ↓ this is just an example.
        val lwjgl64DLLPath = "C:/Users/spoil/Desktop/sussy_items/Evanescent/test_natives/windows/lwjgl64.dll"


        val libraryFile = File(lwjgl64DLLPath)
        if (!libraryFile.exists()) {
            throw IllegalStateException("The lwjgl64.dll library file was not found. Please update the 'lwjgl64DLLPath' variable in Start.kt to point to the correct location.")
        }

        val libraryPath = libraryFile.parentFile.absolutePath.replace('\\', '/')
        System.setProperty("org.lwjgl.librarypath", libraryPath)
    }
}