package net.minecraft.util

import java.util.IllegalFormatException

class StringTranslate {
    private val numericVariablePattern = Regex("%(\\d+\\$)?[\\d.]*[df]")
    private val languageMap: MutableMap<String, String> = mutableMapOf()
    private var lastUpdateTimeInMilliseconds: Long = System.currentTimeMillis()

    companion object {
        @JvmStatic
        val instance = StringTranslate()
    }

    init {
        val inputStream = StringTranslate::class.java.getResourceAsStream("/assets/minecraft/lang/en_US.lang")
        try {
            inputStream?.bufferedReader(Charsets.UTF_8).use { reader ->
                if (reader != null) {
                    for (line in reader.readLines()) {
                        if (line.isNotEmpty() && line[0] != '#') {
                            val parts = line.split('=', limit = 2)
                            if (parts.size == 2) {
                                val key = parts[0]
                                val value = numericVariablePattern.replace(parts[1], "%$1s")
                                languageMap[key] = value
                            }
                        }
                    }
                }
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
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