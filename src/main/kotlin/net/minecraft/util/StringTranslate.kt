package net.minecraft.util

import org.apache.logging.log4j.LogManager
import java.util.IllegalFormatException

object StringTranslate {
    private val logger by lazy { LogManager.getLogger() }

    private val numericVariablePattern = Regex("%(\\d+\\$)?[\\d.]*[df]")
    private val languageMap: MutableMap<String, String> = mutableMapOf()
    private var lastUpdateTimeInMilliseconds: Long = System.currentTimeMillis()

    init {
        StringTranslate::class.java.getResourceAsStream("/assets/minecraft/lang/en_US.lang")
            ?.bufferedReader(Charsets.UTF_8)?.use { reader ->
            reader.readLines().filter { it.isNotEmpty() && it[0] != '#' }.map { it.split('=', limit = 2) }
                .filter { it.size == 2 }.forEach { (key, value) ->
                languageMap[key] = numericVariablePattern.replace(value, "%$1s")
            }
        } ?: logger.error("Error: Null InputStream")
    }

    fun translateKey(key: String): String = languageMap[key] ?: key

    fun translateKeyFormat(key: String, vararg format: Any): String {
        val translatedString = translateKey(key)
        return try {
            String.format(translatedString, *format)
        } catch (e: IllegalFormatException) {
            "Format error: $translatedString"
        }
    }

    fun isKeyTranslated(key: String): Boolean = languageMap.containsKey(key)

    fun getLastUpdateTimeInMilliseconds(): Long = lastUpdateTimeInMilliseconds

    fun replaceWith(newLanguageMap: Map<String, String>) {
        languageMap.clear()
        languageMap.putAll(newLanguageMap)
        lastUpdateTimeInMilliseconds = System.currentTimeMillis()
    }
}