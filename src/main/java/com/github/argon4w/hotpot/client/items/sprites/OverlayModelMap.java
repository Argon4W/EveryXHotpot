package com.github.argon4w.hotpot.client.items.sprites;

import com.github.argon4w.hotpot.EntryStreams;
import com.github.argon4w.hotpot.client.items.sprites.colors.HotpotSpriteColorProviders;
import com.github.argon4w.hotpot.items.components.HotpotSpriteConfigDataComponent;
import com.github.argon4w.hotpot.items.sprites.IHotpotSpriteConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;

public class OverlayModelMap extends HashMap<ResourceLocation, BakedModel> {
    private final BakedModel originalModel;

    public OverlayModelMap(BakedModel originalModel) {
        this.originalModel = originalModel;
    }

    public OverlayModelMap applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
        return entrySet().stream().collect(EntryStreams.of(() -> new OverlayModelMap(originalModel.applyTransform(transformType, poseStack, applyLeftHandTransform))));
    }

    public List<BakedModel> getResolvedTintedModels(ItemStack itemStack, ClientLevel clientLevel, LivingEntity livingEntity, int seed) {
        return HotpotSpriteConfigDataComponent.getSpriteConfigs(itemStack).stream().filter(this::containsConfig).map(config -> getResolvedTintedModel(config, itemStack, clientLevel, livingEntity, seed)).toList();
    }

    public BakedModel getResolvedTintedModel(IHotpotSpriteConfig config, ItemStack itemStack, ClientLevel clientLevel, LivingEntity livingEntity, int seed) {
        return new TintedBakedModel(getAndResolve(config.getProcessorResourceLocation(), itemStack, clientLevel, livingEntity, seed), HotpotSpriteColorProviders.getColor(config));
    }

    public BakedModel getAndResolve(ResourceLocation resourceLocation, ItemStack itemStack, ClientLevel clientLevel, LivingEntity entity, int seed) {
        return resolveOverrides(getOrDefault(resourceLocation, getEmptyModel()), itemStack, clientLevel, entity, seed);
    }

    public BakedModel resolveOverrides(BakedModel model, ItemStack itemStack, ClientLevel clientLevel, LivingEntity entity, int seed) {
        return model.getOverrides().resolve(model, itemStack, clientLevel, entity, seed);
    }

    public BakedModel resolveOriginalModel(ItemStack itemStack, ClientLevel clientLevel, LivingEntity entity, int seed) {
        return originalModel.getOverrides().resolve(originalModel, itemStack, clientLevel, entity, seed);
    }

    public boolean containsConfig(IHotpotSpriteConfig config) {
        return keySet().contains(config.getProcessorResourceLocation());
    }

    public BakedModel getEmptyModel() {
        return new EmptyBakedModel(originalModel);
    }

    public BakedModel getOriginalModel() {
        return originalModel;
    }
}
