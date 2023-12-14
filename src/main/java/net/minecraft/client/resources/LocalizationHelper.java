package net.minecraft.client.resources;

import java.util.Map;

/**
 * A utility class for handling localization in Minecraft.
 */
public class LocalizationHelper {
    private static Locale currentLocale;

    /**
     * Sets the current localization locale.
     *
     * @param locale The locale to set.
     */
    @SuppressWarnings("unused")
    public static void setCurrentLocale(Locale locale) {
        currentLocale = locale;
    }

    /**
     * Translates a message using the current localization locale.
     *
     * @param translateKey The translation key for the message.
     * @param parameters   Optional parameters to replace placeholders in the message.
     * @return The translated message.
     */
    public static String translate(String translateKey, Object... parameters) {
        if (currentLocale == null) throw new IllegalStateException("Current locale has not been set. Call setCurrentLocale() first.");

        return currentLocale.formatTranslation(translateKey, parameters);
    }

    /**
     * Get the properties associated with the current localization locale.
     *
     * @return A map of localization properties (key-value pairs).
     */
    public static Map<String, String> getLocaleProperties() {
        if (currentLocale == null) throw new IllegalStateException("Current locale has not been set. Call setCurrentLocale() first.");

        return currentLocale.getTranslations();
    }
}