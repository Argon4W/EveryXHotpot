package com.github.argon4w.hotpot.soups;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public record HotpotSoupColor(int red, int green, int blue) {
    public static final Serializer SERIALIZER = new Serializer();

    public int toInt() {
        return (red << 16) | (green << 8) | blue;
    }

    public static class Serializer {
        public HotpotSoupColor fromJson(JsonObject jsonObject) {
            if (!jsonObject.has("red")) {
                throw new JsonParseException("Color  must have a \"red\"");
            }

            if (!jsonObject.has("green")) {
                throw new JsonParseException("Color  must have a \"green\"");
            }

            if (!jsonObject.has("blue")) {
                throw new JsonParseException("Color  must have a \"blue\"");
            }

            int red = GsonHelper.getAsInt(jsonObject, "red");
            int green = GsonHelper.getAsInt(jsonObject, "green");
            int blue = GsonHelper.getAsInt(jsonObject, "blue");

            return new HotpotSoupColor(red, green, blue);
        }
    }
}
