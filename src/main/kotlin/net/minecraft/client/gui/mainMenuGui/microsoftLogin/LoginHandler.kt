package net.minecraft.client.gui.mainMenuGui.microsoftLogin

import com.mojang.authlib.Agent
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import me.liuli.elixir.account.MicrosoftAccount
import me.liuli.elixir.compat.OAuthServer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.Session
import org.apache.logging.log4j.LogManager
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.net.BindException
import java.net.Proxy
import java.net.URI

object LoginHandler {
    private lateinit var authServer: OAuthServer
    private val log = LogManager.getLogger()
    private val mc: Minecraft = Minecraft.getMinecraft()
    private var isLoginInProgress = false

    fun initiateLogin() {
        when {
            isLoginInProgress -> retry("Login is already in progress. Retrying...")
            else -> try {
                isLoginInProgress = true.also { setSession() }
            } catch (unfortunateEvent: BindException) {
                retry("Microsoft login handler was trying to run a port but the expected port was already in use. Retrying...")
            }
        }
    }

    private fun setSession() {
        val userAuthentication =
            YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT)

        authServer = MicrosoftAccount.buildFromOpenBrowser(object : MicrosoftAccount.OAuthHandler {
            override fun openUrl(url: String) =
                try {
                    if (!GuiScreen.isShiftKeyDown()) {
                        Desktop.getDesktop().run {
                            if (isSupported(Desktop.Action.BROWSE)) browse(URI(url))
                            else log.error("Opening URL is not supported.")
                        }
                    } else Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(url), null)
                } catch (unfortunateEvent: BindException) {
                    retry("Microsoft login handler was trying to run a port but the expected port was already in use. Retrying...")
                }

            override fun authResult(account: MicrosoftAccount) {
                userAuthentication.setUsername(account.session.username)

                mc.setFreshSession(
                    Session(
                        account.session.username,
                        account.session.uuid,
                        account.session.token,
                    )
                )

                log.info("Authentication successful for user: ${account.session.username}")
                isLoginInProgress = false
            }

            override fun authError(error: String) {
                log.error("Microsoft authentication error: $error")
            //    authServer.stop(isInterrupt = true)
                isLoginInProgress = false
            }
        })
    }

    private fun retry(logMessage: String) {
        log.info(logMessage)
        authServer.stop(isInterrupt = false)
        isLoginInProgress = false
        initiateLogin()
    }
}