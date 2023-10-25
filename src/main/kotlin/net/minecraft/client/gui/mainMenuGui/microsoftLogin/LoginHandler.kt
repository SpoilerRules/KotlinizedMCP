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
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.net.BindException

class LoginHandler (private val copyUrlOnly: Boolean) {
    private lateinit var authServer: OAuthServer
    private val log = LogManager.getLogger()
    private val mc: Minecraft = Minecraft.getMinecraft()

    fun initiateLogin() = setSession()

    private fun setSession() {
        val userAuthentication =
            YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT)

        authServer = MicrosoftAccount.buildFromOpenBrowser(object: MicrosoftAccount.OAuthHandler {
            override fun openUrl(url: String) =
                try {
                    if (!copyUrlOnly) {
                        Desktop.getDesktop().run {
                            if (isSupported(Desktop.Action.BROWSE)) browse(URI(url))
                            else log.error("Opening URL is not supported.")
                        }
                    } else Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(url), null)
                } catch (unfortunateEvent: BindException) {
                    log.error("Microsoft login handler was trying to run a port but the expected port was already in use.")
                    authServer.stop(isInterrupt = true)
                }

            override fun authResult(account: MicrosoftAccount) {
                userAuthentication.setUsername(account.session.username)

                mc.sessionSet(
                    Session(
                        account.session.username,
                        account.session.uuid,
                        account.session.token,
                    )
                )

                log.info("Authentication successful for user: ${account.session.username}")
            }

            override fun authError(error: String) {
                log.error("Microsoft authentication error: $error")
                authServer.stop(isInterrupt = true)
            }
        })
    }
}