package net.minecraft.util

import com.mojang.authlib.GameProfile
import com.mojang.util.UUIDTypeAdapter

data class Session(
    val username: String,
    val playerID: String,
    val token: String,
) {
    fun getSessionID() = "token:$token:$playerID"

    fun getProfile() = runCatching {
        GameProfile(UUIDTypeAdapter.fromString(playerID), username)
    }.getOrElse { GameProfile(null, username) }
}