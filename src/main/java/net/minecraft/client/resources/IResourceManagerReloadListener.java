package net.minecraft.client.resources;

import java.io.IOException;

public interface IResourceManagerReloadListener
{
    void onResourceManagerReload(IResourceManager resourceManager) throws IOException;
}
