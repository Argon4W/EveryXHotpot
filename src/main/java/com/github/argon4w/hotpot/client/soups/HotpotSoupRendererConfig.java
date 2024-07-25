package com.github.argon4w.hotpot.client.soups;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HotpotSoupRendererConfig {
    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation soupModelResourceLocation;
    private final boolean fixedLighting;
    private final List<IHotpotSoupCustomElementRenderer> customElementRenderers;
    private final HotpotSoupColor color;

    public HotpotSoupRendererConfig(ResourceLocation soupModelResourceLocation, boolean fixedLighting, List<IHotpotSoupCustomElementRenderer> customElementRenderers, HotpotSoupColor color) {
        this.soupModelResourceLocation = soupModelResourceLocation;
        this.fixedLighting = fixedLighting;
        this.customElementRenderers = customElementRenderers;
        this.color = color;
    }

    public Optional<ResourceLocation> getSoupModelResourceLocation() {
        return Optional.ofNullable(soupModelResourceLocation);
    }

    public boolean isFixedLighting() {
        return fixedLighting;
    }

    public List<IHotpotSoupCustomElementRenderer> getCustomElementRenderers() {
        return customElementRenderers;
    }

    public Optional<HotpotSoupColor> getColor() {
        return Optional.ofNullable(color);
    }

    public static class Serializer {
        public HotpotSoupRendererConfig fromJson(JsonObject jsonObject) {
            if (jsonObject.has("soup_model_resource_location") && !ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(jsonObject, "soup_model_resource_location"))) {
                throw new JsonParseException("\"soup_model_resource_location\" in the soup renderer config must be a valid resource location");
            }

            ResourceLocation soupModelResourceLocation = null;
            boolean fixedLighting = false;
            ArrayList<IHotpotSoupCustomElementRenderer> customElements = Lists.newArrayList();

            if (jsonObject.has("soup_model_resource_location")) {
                soupModelResourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "soup_model_resource_location"));
            }

            if (jsonObject.has("fixed_lighting")) {
                fixedLighting = GsonHelper.getAsBoolean(jsonObject, "fixed_lighting");
            }

            for (JsonElement jsonElement : GsonHelper.getAsJsonArray(jsonObject, "custom_elements_renderers", new JsonArray())) {
                if (!jsonElement.isJsonObject()) {
                    throw new JsonParseException("Custom element renderer in the soup renderer config must be a JSON object");
                }

                JsonObject customRendererJsonObject = jsonElement.getAsJsonObject();

                if (!customRendererJsonObject.has("type")) {
                    throw new JsonParseException("Custom element renderer in the soup renderer config must have a \"type\"");
                }

                if (!ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(customRendererJsonObject, "type"))) {
                    throw new JsonParseException("\"type\" in the custom element renderer must be a valid resource location");
                }

                ResourceLocation customElementRendererResourceLocation = new ResourceLocation(GsonHelper.getAsString(customRendererJsonObject, "type"));
                IHotpotSoupCustomElementRendererSerializer<?> serializer = HotpotSoupCustomElements.getCustomElementSerializer(customElementRendererResourceLocation);

                customElements.add(serializer.fromJson(customRendererJsonObject));
            }

            if (!jsonObject.has("color")) {
                return new HotpotSoupRendererConfig(soupModelResourceLocation, fixedLighting, customElements, null);
            }

            if (!jsonObject.get("color").isJsonObject()) {
                throw new JsonParseException("\"color\" in the soup renderer config must be a JSON object");
            }

            HotpotSoupColor color = HotpotSoupColor.SERIALIZER.fromJson(GsonHelper.getAsJsonObject(jsonObject, "color"));

            return new HotpotSoupRendererConfig(soupModelResourceLocation, fixedLighting, customElements, color);
        }
    }
}