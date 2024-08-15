package com.github.argon4w.hotpot.client.items.sprites;

import com.github.argon4w.hotpot.client.HotpotColor;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("deprecation")
public record TintedRenderPassBakedModel(BakedModel model, HotpotColor color) implements BakedModel {
    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pDirection, @NotNull RandomSource pRandom) {
        return model.getQuads(pState, pDirection, pRandom).stream().<BakedQuad>map(bakedQuad -> new TintedBakedQuad(bakedQuad, color)).toList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return model.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return model.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return model.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return model.isCustomRenderer();
    }

    @NotNull
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return model.getParticleIcon();
    }

    @NotNull
    @Override
    public ItemTransforms getTransforms() {
        return model.getTransforms();
    }

    @NotNull
    @Override
    public ItemOverrides getOverrides() {
        return model.getOverrides();
    }

    @NotNull
    @Override
    public List<BakedModel> getRenderPasses(@NotNull ItemStack itemStack, boolean fabulous) {
        return model.getRenderPasses(itemStack, fabulous);
    }
}
