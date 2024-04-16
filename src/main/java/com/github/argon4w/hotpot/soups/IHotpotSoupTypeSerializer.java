package com.github.argon4w.hotpot.soups;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface IHotpotSoupTypeSerializer<T extends IHotpotSoupType> {
    IHotpotSoupFactory<T> fromJson(ResourceLocation resourceLocation, JsonObject jsonObject);
    IHotpotSoupFactory<T> fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf byteBuf);
    void toNetwork(IHotpotSoupFactory<T> t, FriendlyByteBuf byteBuf);
}
