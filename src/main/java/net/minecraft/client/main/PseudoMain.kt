package net.minecraft.client.main

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mojang.authlib.properties.PropertyMap
import com.mojang.authlib.properties.PropertyMap.Serializer
import joptsimple.OptionParser
import joptsimple.OptionSet
import joptsimple.OptionSpec
import net.minecraft.client.Minecraft
import net.minecraft.util.Session
import java.io.File
import java.net.Authenticator
import java.net.InetSocketAddress
import java.net.PasswordAuthentication
import java.net.Proxy
import java.net.Proxy.Type

class PseudoMain(private val args: Array<String>) {
    fun main() {
        System.setProperty("java.net.preferIPv4Stack", "true")
        val optionParser = OptionParser()
        optionParser.allowsUnrecognizedOptions()
        optionParser.accepts("demo")
        optionParser.accepts("fullscreen")
        optionParser.accepts("checkGlErrors")
        val serverOption: OptionSpec<String> = optionParser.accepts("server").withRequiredArg()
        val portOption: OptionSpec<Int> =
            optionParser.accepts("port").withRequiredArg().ofType(Int::class.java).defaultsTo(25565)
        val gameDirOption: OptionSpec<File> =
            optionParser.accepts("gameDir").withRequiredArg().ofType(File::class.java).defaultsTo(File("."))
        val assetsDirOption: OptionSpec<File> =
            optionParser.accepts("assetsDir").withRequiredArg().ofType(File::class.java)
        val resourcePackDirOption: OptionSpec<File> =
            optionParser.accepts("resourcePackDir").withRequiredArg().ofType(File::class.java)
        val proxyHostOption: OptionSpec<String> = optionParser.accepts("proxyHost").withRequiredArg()
        val proxyPortOption: OptionSpec<Int> =
            optionParser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", *arrayOf()).ofType(Int::class.java)
        val proxyUserOption: OptionSpec<String> = optionParser.accepts("proxyUser").withRequiredArg()
        val proxyPassOption: OptionSpec<String> = optionParser.accepts("proxyPass").withRequiredArg()
        val usernameOption: OptionSpec<String> =
            optionParser.accepts("username").withRequiredArg().defaultsTo("Player" + System.currentTimeMillis() % 1000L)
        val uuidOption: OptionSpec<String> = optionParser.accepts("uuid").withRequiredArg()
        val accessTokenOption: OptionSpec<String> = optionParser.accepts("accessToken").withRequiredArg().required()
        val versionOption: OptionSpec<String> = optionParser.accepts("version").withRequiredArg().required()
        val widthOption: OptionSpec<Int> =
            optionParser.accepts("width").withRequiredArg().ofType(Int::class.java).defaultsTo(854)
        val heightOption: OptionSpec<Int> =
            optionParser.accepts("height").withRequiredArg().ofType(Int::class.java).defaultsTo(480)
        val userPropertiesOption: OptionSpec<String> =
            optionParser.accepts("userProperties").withRequiredArg().defaultsTo("{}")
        val profilePropertiesOption: OptionSpec<String> =
            optionParser.accepts("profileProperties").withRequiredArg().defaultsTo("{}")
        val assetIndexOption: OptionSpec<String> = optionParser.accepts("assetIndex").withRequiredArg()
        val userTypeOption: OptionSpec<String> = optionParser.accepts("userType").withRequiredArg().defaultsTo("legacy")
        val nonOptions: OptionSpec<String> = optionParser.nonOptions()
        val optionSet: OptionSet = optionParser.parse(*args)
        val ignoredArgs: List<String> = optionSet.valuesOf(nonOptions)

        if (ignoredArgs.isNotEmpty()) {
            println("Completely ignored arguments: $ignoredArgs")
        }

        val proxyHost: String? = optionSet.valueOf(proxyHostOption)
        val proxy: Proxy = if (!proxyHost.isNullOrEmpty()) {
            try {
                Proxy(Type.SOCKS, InetSocketAddress(proxyHost, optionSet.valueOf(proxyPortOption)))
            } catch (e: Exception) {
                e.printStackTrace()
                Proxy.NO_PROXY
            }
        } else {
            Proxy.NO_PROXY
        }

        val proxyUser: String? = optionSet.valueOf(proxyUserOption)
        val proxyPass: String? = optionSet.valueOf(proxyPassOption)
        if (proxy != Proxy.NO_PROXY && !proxyUser.isNullOrEmpty() && !proxyPass.isNullOrEmpty()) {
            Authenticator.setDefault(object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(proxyUser, proxyPass.toCharArray())
                }
            })
        }

        val width: Int = optionSet.valueOf(widthOption)
        val height: Int = optionSet.valueOf(heightOption)
        val fullscreen: Boolean = optionSet.has("fullscreen")
        val checkGlErrors: Boolean = optionSet.has("checkGlErrors")
        val demo: Boolean = optionSet.has("demo")
        val version: String = optionSet.valueOf(versionOption)
        val gson: Gson = GsonBuilder().registerTypeAdapter(PropertyMap::class.java, Serializer()).create()
        val userProperties: PropertyMap =
            gson.fromJson(optionSet.valueOf(userPropertiesOption), PropertyMap::class.java)
        val profileProperties: PropertyMap =
            gson.fromJson(optionSet.valueOf(profilePropertiesOption), PropertyMap::class.java)
        val gameDir: File = optionSet.valueOf(gameDirOption)
        val assetsDir: File = optionSet.valueOf(assetsDirOption) ?: File(gameDir, "assets/")
        val resourcePackDir: File = optionSet.valueOf(resourcePackDirOption) ?: File(gameDir, "resourcepacks/")
        val uuid: String = optionSet.valueOf(uuidOption) ?: optionSet.valueOf(usernameOption)
        val assetIndex: String? = optionSet.valueOf(assetIndexOption)
        val server: String? = optionSet.valueOf(serverOption)
        val port: Int = optionSet.valueOf(portOption)
        val username: String = optionSet.valueOf(usernameOption)
        val accessToken: String = optionSet.valueOf(accessTokenOption)

        val session = Session(username, uuid, accessToken)
        val gameConfiguration = GameConfiguration(
            GameConfiguration.UserInformation(session, userProperties, profileProperties, proxy),
            GameConfiguration.DisplayInformation(width, height, fullscreen, checkGlErrors),
            GameConfiguration.FolderInformation(gameDir, resourcePackDir, assetsDir, assetIndex),
            GameConfiguration.GameInformation(demo, version),
            GameConfiguration.ServerInformation(server, port)
        )

        Runtime.getRuntime().addShutdownHook(Thread(ThreadGroup("Client Shutdown Thread Group")) {
            Minecraft.stopIntegratedServer()
        })

        Thread.currentThread().name = "Client thread"
        Minecraft(gameConfiguration).run()
    }
}