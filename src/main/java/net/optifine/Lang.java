package net.optifine;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import net.minecraft.client.resources.LocalizationHelper;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Lang
{
    private static final Splitter splitter = Splitter.on('=').limit(2);
    private static final Pattern pattern = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");

    public static void resourcesReloaded()
    {
        Map map = LocalizationHelper.getLocaleProperties();
        List<String> list = new ArrayList();
        String s = "optifine/lang/";
        String s1 = "en_US";
        String s2 = ".lang";
        list.add(s + s1 + s2);

        if (!Config.getGameSettings().language.equals(s1))
        {
            list.add(s + Config.getGameSettings().language + s2);
        }

        String[] astring = (String[])((String[])list.toArray(new String[list.size()]));
        loadResources(Config.getDefaultResourcePack(), astring, map);
        IResourcePack[] airesourcepack = Config.getResourcePacks();

        for (int i = 0; i < airesourcepack.length; ++i)
        {
            IResourcePack iresourcepack = airesourcepack[i];
            loadResources(iresourcepack, astring, map);
        }
    }

    private static void loadResources(IResourcePack rp, String[] files, Map localeProperties)
    {
        try
        {
            for (int i = 0; i < files.length; ++i)
            {
                String s = files[i];
                ResourceLocation resourcelocation = new ResourceLocation(s);

                if (rp.resourceExists(resourcelocation))
                {
                    InputStream inputstream = rp.getInputStream(resourcelocation);

                    if (inputstream != null)
                    {
                        loadLocaleData(inputstream, localeProperties);
                    }
                }
            }
        }
        catch (IOException ioexception)
        {
            ioexception.printStackTrace();
        }
    }

    public static void loadLocaleData(InputStream is, Map localeProperties) throws IOException
    {
        Iterator iterator = IOUtils.readLines(is, StandardCharsets.UTF_8).iterator();
        is.close();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();

            if (!s.isEmpty() && s.charAt(0) != 35)
            {
                String[] astring = (String[])((String[])Iterables.toArray(splitter.split(s), String.class));

                if (astring != null && astring.length == 2)
                {
                    String s1 = astring[0];
                    String s2 = pattern.matcher(astring[1]).replaceAll("%$1s");
                    localeProperties.put(s1, s2);
                }
            }
        }
    }

    public static String get(String key)
    {
        return LocalizationHelper.translate(key, new Object[0]);
    }

    public static String get(String key, String def)
    {
        String s = LocalizationHelper.translate(key, new Object[0]);
        return s != null && !s.equals(key) ? s : def;
    }

    public static String getOn()
    {
        return LocalizationHelper.translate("options.on", new Object[0]);
    }

    public static String getOff()
    {
        return LocalizationHelper.translate("options.off", new Object[0]);
    }

    public static String getFast()
    {
        return LocalizationHelper.translate("options.graphics.fast", new Object[0]);
    }

    public static String getFancy()
    {
        return LocalizationHelper.translate("options.graphics.fancy", new Object[0]);
    }

    public static String getDefault()
    {
        return LocalizationHelper.translate("generator.default", new Object[0]);
    }
}
