package net.minecraft.util

import com.google.common.base.Splitter
import com.google.common.collect.Maps
import org.apache.commons.io.IOUtils
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.regex.Pattern

class StringTranslate {
    private val numericVariablePattern = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]")
    private val equalSignSplitter = Splitter.on('=').limit(2)
    private val languageList: MutableMap<String, String> = Maps.newHashMap()
    private var lastUpdateTimeInMilliseconds: Long = System.currentTimeMillis()

    companion object {
        private val instance = StringTranslate()
        @JvmStatic
        fun getInstance(): StringTranslate = instance
    }

    @Synchronized
    fun translateKey(key: String): String = tryTranslateKey(key)

    @Synchronized
    fun translateKeyFormat(key: String, vararg format: Any): String {
        val s = tryTranslateKey(key)
        return try {
            String.format(s, *format)
        } catch (e: IllegalFormatException) {
            "Format error: $s"
        }
    }

    private fun tryTranslateKey(key: String): String = languageList[key] ?: key

    @Synchronized
    fun isKeyTranslated(key: String): Boolean = languageList.containsKey(key)

    fun getLastUpdateTimeInMilliseconds(): Long = lastUpdateTimeInMilliseconds

    @Synchronized
    fun replaceWith(p0: Map<String, String>) {
        languageList.clear()
        languageList.putAll(p0)
        lastUpdateTimeInMilliseconds = System.currentTimeMillis()
    }

    init {
        val inputstream = StringTranslate::class.java.getResourceAsStream("/assets/minecraft/lang/en_US.lang")
        try {
            for (s in IOUtils.readLines(inputstream, StandardCharsets.UTF_8)) {
                if (s.isNotEmpty() && s[0] != '#') {
                    val astring = equalSignSplitter.split(s).toList().toTypedArray()
                    if (astring.size == 2) {
                        val s1 = astring[0]
                        val s2 = numericVariablePattern.matcher(astring[1]).replaceAll("%$1s")
                        languageList[s1] = s2
                    }
                }
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }
}