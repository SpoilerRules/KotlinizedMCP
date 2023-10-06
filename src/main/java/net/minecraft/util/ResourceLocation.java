package net.minecraft.util;

import java.util.Objects;

public class ResourceLocation
{
    protected final String resourceDomain;
    protected final String resourcePath;

    protected ResourceLocation(int p_i45928_1_, String... resourceName) {
        this.resourceDomain = org.apache.commons.lang3.StringUtils.isEmpty(resourceName[0]) ? "minecraft" : resourceName[0].toLowerCase();
        this.resourcePath = Objects.requireNonNull(resourceName[1]);
    }

    public ResourceLocation(String resourceName)
    {
        this(0, splitObjectName(resourceName));
    }

    public ResourceLocation(String resourceDomainIn, String resourcePathIn)
    {
        this(0, resourceDomainIn, resourcePathIn);
    }

    /**
     * Splits an object name (such as minecraft:apple) into the domain and path parts and returns these as an array of
     * length 2. If no colon is present in the passed value, the returned array will contain {null, objectName}.
     */
    protected static String[] splitObjectName(String objectName) {
        String[] objectParts = {null, objectName};
        int delimiterIndex = objectName.indexOf(":");

        if (delimiterIndex >= 0) {
            objectParts[1] = objectName.substring(delimiterIndex + 1);

            if (delimiterIndex > 1) {
                objectParts[0] = objectName.substring(0, delimiterIndex);
            }
        }

        return objectParts;
    }

    public String getResourcePath()
    {
        return this.resourcePath;
    }

    public String getResourceDomain()
    {
        return this.resourceDomain;
    }

    public String toString()
    {
        return this.resourceDomain + ':' + this.resourcePath;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof ResourceLocation resourcelocation))
        {
            return false;
        }
        else
        {
            return this.resourceDomain.equals(resourcelocation.resourceDomain) && this.resourcePath.equals(resourcelocation.resourcePath);
        }
    }

    public int hashCode()
    {
        return 31 * this.resourceDomain.hashCode() + this.resourcePath.hashCode();
    }
}
