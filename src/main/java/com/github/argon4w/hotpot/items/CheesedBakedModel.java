package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotTagsHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public record CheesedBakedModel(BakedModel originalModel, BakedModel cheeseModel) implements BakedModel {
    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, Random randomSource) {
        return originalModel.getQuads(state, direction, randomSource);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return originalModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return originalModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return originalModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return originalModel.isCustomRenderer();
    }

    @NotNull
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return originalModel.getParticleIcon();
    }

    @NotNull
    @Override
    public ItemOverrides getOverrides() {
        return new ItemOverrides() {
            @Nullable
            @Override
            public BakedModel resolve(BakedModel bakedModel, ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int p_173469_) {
                return isCheesed(itemStack) ?
                        cheeseModel.getOverrides().resolve(cheeseModel, itemStack, clientLevel, livingEntity, p_173469_) :
                        originalModel.getOverrides().resolve(originalModel, itemStack, clientLevel, livingEntity, p_173469_);
            }
        };
    }

    @NotNull
    @Override
    public ItemTransforms getTransforms() {
        return originalModel.getTransforms();
    }

    private boolean isCheesed(ItemStack itemStack) {
        return HotpotTagsHelper.hasHotpotTag(itemStack) && HotpotTagsHelper.getHotpotTag(itemStack).contains("Cheesed", Tag.TAG_ANY_NUMERIC) && HotpotTagsHelper.getHotpotTag(itemStack).getBoolean("Cheesed");
    }
}
