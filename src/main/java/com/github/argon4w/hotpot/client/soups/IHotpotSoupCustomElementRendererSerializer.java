package com.github.argon4w.hotpot.client.soups;

import com.google.gson.JsonObject;

public interface IHotpotSoupCustomElementRendererSerializer<T extends IHotpotSoupCustomElementRenderer> {
    T fromJson(JsonObject jsonObject);
}
