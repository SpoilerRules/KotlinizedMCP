package net.minecraft.client.gui.mainMenuGui.microsoftLogin

import java.awt.Desktop
import java.net.Proxy
import java.net.URI

import com.mojang.authlib.Agent
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService

import net.minecraft.client.Minecraft
import net.minecraft.util.Session

import me.liuli.elixir.account.MicrosoftAccount
import me.liuli.elixir.compat.OAuthServer

import org.apache.logging.log4j.LogManager

class LoginHandler {
    private lateinit var authServer: OAuthServer
    private val log = LogManager.getLogger()
    private val mc: Minecraft = Minecraft.getMinecraft()

    fun initiateLogin() = setSession()

    private fun setSession() {
        val userAuthentication =
            YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT)

        authServer = MicrosoftAccount.buildFromOpenBrowser(object: MicrosoftAccount.OAuthHandler {
            override fun openUrl(url: String) =
                Desktop.getDesktop().run {
                    if (isSupported(Desktop.Action.BROWSE)) browse(URI(url))
                    else log.error("Opening URL is not supported.")
                }

            override fun authResult(account: MicrosoftAccount) {
                userAuthentication.setUsername(account.session.username)

                mc.sessionSet(
                    Session(
                        account.session.username,
                        account.session.uuid,
                        account.session.token,
                        "microsoft"
                    )
                )

                log.info("Authentication successful for user: ${account.session.username}")
            }

            override fun authError(error: String) = log.error("Microsoft authentication error: $error")
        })
    }
}