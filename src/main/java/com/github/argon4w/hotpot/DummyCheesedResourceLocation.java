package com.github.argon4w.hotpot;


import net.minecraft.resources.ResourceLocation;

public class DummyCheesedResourceLocation extends ResourceLocation {
    private final ResourceLocation originalTextureLocation;

    public DummyCheesedResourceLocation(ResourceLocation originalTextureLocation, String namespace, String path) {
        super(namespace, path);
        this.originalTextureLocation = originalTextureLocation;
    }

    public ResourceLocation getOriginalTextureLocation() {
        return originalTextureLocation;
    }
}
