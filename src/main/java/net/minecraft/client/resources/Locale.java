package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Locale {
    private static final Splitter KEY_VALUE_SPLITTER = Splitter.on('=').limit(2);
    private static final Pattern FORMAT_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");
    public final Map<String, String> translations = Maps.newHashMap();
    private boolean isUnicode;

    public synchronized void loadTranslations(IResourceManager resourceManager, List<String> languages) throws IOException {
        translations.clear();

        for (String language : languages) {
            String languageFile = String.format("lang/%s.lang", language);

            for (String resourceDomain : resourceManager.getResourceDomains()) {
                ResourceLocation resourceLocation = new ResourceLocation(resourceDomain, languageFile);

                loadTranslationFiles(resourceManager.getAllResources(resourceLocation));
            }
        }

        checkUnicodeStatus();
    }

    public boolean isUnicode() {
        return this.isUnicode;
    }

    private void checkUnicodeStatus() {
        int unicodeCount = 0;
        int totalCount = 0;

        for (String value : translations.values()) {
            int length = value.length();
            totalCount += length;

            for (int i = 0; i < length; ++i) {
                if (value.charAt(i) >= 256) {
                    ++unicodeCount;
                }
            }
        }

        float unicodeRatio = (float) unicodeCount / totalCount;
        isUnicode = unicodeRatio > 0.1;
    }

    private void loadTranslationFiles(List<IResource> resources) {
        for (IResource resource : resources) {
            InputStream inputStream = resource.getInputStream();

            try {
                loadTranslationData(inputStream);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    private void loadTranslationData(InputStream inputStream) {
        for (String line : IOUtils.readLines(inputStream, StandardCharsets.UTF_8)) {
            if (!line.isEmpty() && line.charAt(0) != '#') {
                String[] keyValue = Iterables.toArray(KEY_VALUE_SPLITTER.split(line), String.class);

                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = FORMAT_PATTERN.matcher(keyValue[1]).replaceAll("%$1s");
                    translations.put(key, value);
                }
            }
        }
    }

    private String translateKey(String key) {
        String translation = translations.get(key);
        return translation == null ? key : translation;
    }

    public String formatTranslation(String key, Object[] parameters) {
        String translation = translateKey(key);

        try {
            return String.format(translation, parameters);
        } catch (IllegalFormatException e) {
            return "Format error: " + translation;
        }
    }

    public Map<String, String> getTranslations() {
        return translations;
    }
}