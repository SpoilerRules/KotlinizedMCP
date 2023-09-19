package net.minecraft.client.launch

import net.minecraft.client.main.PseudoMain as Main

object Start {
    @JvmStatic
    fun main(args: Array<String>) {
        setLwjglLibraryPath()
        val defaultArgs = arrayOf(
            "--username", "little_haxor", // Modifiable
            "--version", "Evanescent", // Modifiable
            "--assetsDir", "assets",
            "--assetIndex", "1.8",
            "--uuid", "41cdf1dc-19cd-460e-92d8-5e5dd13848ad",
            "--accessToken", "0", // Modify this if you are going to modify user type.
            "--userProperties", "{}",
            "--userType", "offline" // Modify access token to be able to modify this.
        )
        val combinedArgs = concat(args, defaultArgs)
        Main(combinedArgs).main()
    }

    private fun setLwjglLibraryPath() {
    val os = System.getProperty("os.name").lowercase()
        val libraryPath = if (os.contains("win"))
            "C:/Users/spoil/Desktop/sussy_items/Evanescent/test_natives/windows" // Replace with your path
        else if (os.contains("nix") || os.contains("nux"))
            "C:/Users/spoil/Desktop/sussy_items/Evanescent/test_natives/linux" // Replace with your path
        else throw UnsupportedOperationException("Unsupported operating system: $os")

        System.setProperty("org.lwjgl.librarypath", libraryPath)
    }

    private fun concat(first: Array<String>, second: Array<String>): Array<String> {
        val result = Array(first.size + second.size) { "" }
        System.arraycopy(first, 0, result, 0, first.size)
        System.arraycopy(second, 0, result, first.size, second.size)
        return result
    }
}