package net.minecraft.client.main

import annotations.ExperimentalState
import com.google.gson.GsonBuilder
import com.mojang.authlib.properties.PropertyMap
import joptsimple.OptionParser
import joptsimple.OptionSpec
import net.minecraft.client.GameInitializer
import net.minecraft.client.Minecraft
import net.minecraft.client.main.GameConfiguration.*
import net.minecraft.util.Session
import java.io.File
import java.net.Authenticator
import java.net.InetSocketAddress
import java.net.PasswordAuthentication
import java.net.Proxy

@ExperimentalState
object ClientInitializer {
    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty("java.net.preferIPv4Stack", "true")

        val optionParser = OptionParser().apply {
            allowsUnrecognizedOptions()
            accepts("fullscreen")
            accepts("checkGlErrors")
        }

        val serverOption: OptionSpec<String> = optionParser.accepts("server").withRequiredArg()
        val portOption: OptionSpec<Int> = optionParser.accepts("port").withRequiredArg().ofType(
            Int::class.java
        ).defaultsTo(25565)
        val gameDirOption: OptionSpec<String> = optionParser.accepts("gameDir").withRequiredArg().defaultsTo(".")
        val assetsDirOption: OptionSpec<String> = optionParser.accepts("assetsDir").withRequiredArg()
        val resourcePackDirOption: OptionSpec<String> = optionParser.accepts("resourcePackDir").withRequiredArg()
        val proxyHostOption: OptionSpec<String> = optionParser.accepts("proxyHost").withRequiredArg()
        val proxyPortOption: OptionSpec<Int> = optionParser.accepts("proxyPort").withRequiredArg().defaultsTo("8080").ofType(Int::class.java)
        val proxyUserOption: OptionSpec<String> = optionParser.accepts("proxyUser").withRequiredArg()
        val proxyPassOption: OptionSpec<String> = optionParser.accepts("proxyPass").withRequiredArg()
        val usernameOption: OptionSpec<String> = optionParser.accepts("username").withRequiredArg().defaultsTo("mrflashstudio")
        val uuidOption: OptionSpec<String> = optionParser.accepts("uuid").withRequiredArg()
        val accessTokenOption: OptionSpec<String> = optionParser.accepts("accessToken").withRequiredArg().required()
        val versionOption: OptionSpec<String> = optionParser.accepts("version").withRequiredArg().required()
        val widthOption: OptionSpec<Int> = optionParser.accepts("width").withRequiredArg().ofType(Int::class.java).defaultsTo(854)
        val heightOption: OptionSpec<Int> = optionParser.accepts("height").withRequiredArg().ofType(Int::class.java).defaultsTo(480)
        val userPropertiesOption: OptionSpec<String> = optionParser.accepts("userProperties").withRequiredArg().defaultsTo("{}")
        val profilePropertiesOption: OptionSpec<String> = optionParser.accepts("profileProperties").withRequiredArg().defaultsTo("{}")
        val assetIndexOption: OptionSpec<String> = optionParser.accepts("assetIndex").withRequiredArg()

        val optionSet = optionParser.parse(*args)

        val remainingOptions: OptionSpec<String> = optionParser.nonOptions()

        optionSet.valuesOf(remainingOptions).takeIf { it.isNotEmpty() }?.let {
            println("Completely ignored arguments: $it")
        }

        val proxyHost = optionSet.valueOf(proxyHostOption)
        val proxy = proxyHost?.let {
            try {
                Proxy(Proxy.Type.SOCKS, InetSocketAddress(it, optionSet.valueOf(proxyPortOption)))
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        } ?: Proxy.NO_PROXY

        val proxyUser = optionSet.valueOf(proxyUserOption)
        val proxyPass = optionSet.valueOf(proxyPassOption)

        if (proxy != Proxy.NO_PROXY && proxyUser.isNotEmpty() && proxyPass.isNotEmpty()) {
            Authenticator.setDefault(object : Authenticator() {
                override fun getPasswordAuthentication() = PasswordAuthentication(proxyUser, proxyPass.toCharArray())
            })
        }

        val width = optionSet.valueOf(widthOption)
        val height = optionSet.valueOf(heightOption)
        val fullscreen = optionSet.has("fullscreen")
        val checkGlErrors = optionSet.has("checkGlErrors")
        val version = optionSet.valueOf(versionOption)

        val gson = GsonBuilder().registerTypeAdapter(PropertyMap::class.java, PropertyMap.Serializer()).create()
        val userProperties = gson.fromJson(optionSet.valueOf(userPropertiesOption), PropertyMap::class.java)
        val profileProperties = gson.fromJson(optionSet.valueOf(profilePropertiesOption), PropertyMap::class.java)

        val gameDir = File(optionSet.valueOf(gameDirOption))
        val assetsDir =
            if (optionSet.has(assetsDirOption)) File(optionSet.valueOf(assetsDirOption)) else File(gameDir, "assets/")
        val resourcePackDir =
            if (optionSet.has(resourcePackDirOption)) File(optionSet.valueOf(resourcePackDirOption)) else File(
                gameDir,
                "resourcepacks/"
            )

        val uuid = optionSet.valueOf(uuidOption) ?: optionSet.valueOf(usernameOption)
        val assetIndex = optionSet.valueOf(assetIndexOption)

        val server = optionSet.valueOf(serverOption)
        val port = optionSet.valueOf(portOption)

        val session = Session(optionSet.valueOf(usernameOption), uuid, optionSet.valueOf(accessTokenOption))

        val gameConfiguration = GameConfiguration(
            UserInformation(session, userProperties, profileProperties, proxy),
            DisplayInformation(width, height, fullscreen, checkGlErrors),
            FolderInformation(gameDir, resourcePackDir, assetsDir, assetIndex),
            GameInformation(version),
            ServerInformation(server, port)
        )

        Runtime.getRuntime().addShutdownHook(Thread {
            Minecraft.stopIntegratedServer()
        }.apply { name = "Client Shutdown Thread" })

        Thread.currentThread().apply { name = "Client thread" }

        Minecraft(gameConfiguration)

        GameInitializer().runGame()
    }
}