package net.minecraft.client.resources;

import java.io.IOException;
import java.util.List;

public interface IReloadableResourceManager extends IResourceManager
{
    void reloadResources(List<IResourcePack> resourcesPacksList) throws IOException;

    void registerReloadListener(IResourceManagerReloadListener reloadListener) throws IOException;
}
