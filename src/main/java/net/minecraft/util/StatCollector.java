package net.minecraft.util;

public class StatCollector {

    public static String translateToLocal(String key)
    {
        return StringTranslate.INSTANCE.translateKey(key);
    }

    public static String translateToLocalFormatted(String key, Object... format)
    {
        return StringTranslate.INSTANCE.translateKeyFormat(key, format);
    }

    public static boolean canTranslate(String key)
    {
        return StringTranslate.INSTANCE.isKeyTranslated(key);
    }

    public static long getLastTranslationUpdateTimeInMilliseconds()
    {
        return StringTranslate.INSTANCE.getLastUpdateTimeInMilliseconds();
    }
}
