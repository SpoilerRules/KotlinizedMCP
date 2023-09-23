package net.minecraft.client.gui.mainMenuGui.microsoftLogin

import com.mojang.authlib.Agent
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator
import net.minecraft.util.Session
import org.apache.logging.log4j.LogManager
import java.net.Proxy

class LoginHandler {
    private val microsoftAuthenticator = MicrosoftAuthenticator()
    private val log = LogManager.getLogger()

    fun initiateLogin(): Session {
        val authResult = microsoftAuthenticator.loginWithWebview()
        val minecraftProfile = authResult.profile
        val userSession = Session(minecraftProfile.name, minecraftProfile.id, authResult.accessToken, "microsoft")
        val userAuthentication = YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT)

        userAuthentication.setUsername(minecraftProfile.name)

        log.info("Profile Name: ${minecraftProfile.name}")
        log.info("Profile ID: ${minecraftProfile.id}")
        log.info("Access Token: ${authResult.accessToken}")
        log.info("User Session created: $userSession")
        log.info("User Authentication Profile: $userAuthentication")
        log.info("Successful Login")

        return userSession
    }
}