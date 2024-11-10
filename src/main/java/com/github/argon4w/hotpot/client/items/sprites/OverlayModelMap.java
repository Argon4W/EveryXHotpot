package com.github.argon4w.hotpot.client.items.sprites;

import com.github.argon4w.fancytoys.streams.EntryStream;
import com.github.argon4w.hotpot.api.items.sprites.IHotpotSpriteConfig;
import com.github.argon4w.hotpot.client.items.sprites.colors.HotpotSpriteColorProviders;
import com.github.argon4w.hotpot.client.items.sprites.processors.providers.HotpotSpriteProcessorProviders;
import com.github.argon4w.hotpot.items.components.HotpotSpriteConfigDataComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class OverlayModelMap extends HashMap<ResourceLocation, BakedModel> {

    private final BakedModel originalModel;

    public OverlayModelMap(BakedModel originalModel) {
        this.originalModel = originalModel;
    }

    public BakedModel getResolvedTintedModel(
            IHotpotSpriteConfig config,
            ItemStack itemStack,
            ClientLevel clientLevel,
            LivingEntity livingEntity,
            int seed) {
        return new TintedBakedModel(getAndResolve(
                HotpotSpriteProcessorProviders.getProcessorResourceLocation(config),
                itemStack,
                clientLevel,
                livingEntity,
                seed), HotpotSpriteColorProviders.getColor(config));
    }

    public BakedModel getAndResolve(
            ResourceLocation resourceLocation,
            ItemStack itemStack,
            ClientLevel clientLevel,
            LivingEntity entity,
            int seed) {
        return resolveOverrides(
                getOrDefault(resourceLocation, getEmptyModel()),
                itemStack,
                clientLevel,
                entity,
                seed);
    }

    public List<BakedModel> getResolvedTintedModels(
            ItemStack itemStack,
            ClientLevel clientLevel,
            LivingEntity livingEntity,
            int seed) {
        return HotpotSpriteConfigDataComponent
                .getSpriteConfigs(itemStack)
                .stream()
                .filter(this::containsConfig)
                .map(config -> getResolvedTintedModel(config, itemStack, clientLevel, livingEntity, seed))
                .toList();
    }

    public BakedModel resolveOverrides(
            BakedModel model,
            ItemStack itemStack,
            ClientLevel clientLevel,
            LivingEntity entity,
            int seed) {
        return model.getOverrides().resolve(model, itemStack, clientLevel, entity, seed);
    }

    public BakedModel resolveOriginalModel(
            BakedModel bakedModel,
            ItemStack itemStack,
            ClientLevel clientLevel,
            LivingEntity entity,
            int seed) {
        return originalModel.getOverrides().resolve(bakedModel, itemStack, clientLevel, entity, seed);
    }

    public OverlayModelMap applyTransform(
            ItemDisplayContext transformType,
            PoseStack poseStack,
            boolean applyLeftHandTransform) {
        return EntryStream
                .fromMap(this)
                .toMap(() -> new OverlayModelMap(originalModel.applyTransform(transformType, poseStack, applyLeftHandTransform)));
    }

    public boolean containsConfig(IHotpotSpriteConfig config) {
        return keySet().contains(HotpotSpriteProcessorProviders.getProcessorResourceLocation(config));
    }

    public BakedModel getEmptyModel() {
        return new EmptyBakedModel(originalModel);
    }

    public BakedModel getOriginalModel() {
        return originalModel;
    }
}
