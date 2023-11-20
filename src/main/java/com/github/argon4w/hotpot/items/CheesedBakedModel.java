package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotTagsHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.Constants;

import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class CheesedBakedModel implements IBakedModel {
    private final IBakedModel originalModel;
    private final IBakedModel cheeseModel;

    public CheesedBakedModel(IBakedModel originalModel, IBakedModel cheeseModel) {
        this.originalModel = originalModel;
        this.cheeseModel = cheeseModel;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction direction, Random randomSource) {
        if (originalModel.getQuads(state, direction, randomSource).size() > 0)
            System.out.println(originalModel.getQuads(state, direction, randomSource).get(0).getSprite().getName());
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

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return originalModel.getParticleIcon();
    }

    @Override
    public ItemOverrideList getOverrides() {

        return new ItemOverrideList() {
            @Override
            public IBakedModel resolve(IBakedModel bakedModel, ItemStack itemStack, ClientWorld clientLevel, LivingEntity livingEntity) {
                return isCheesed(itemStack) ?
                        cheeseModel.getOverrides().resolve(cheeseModel, itemStack, clientLevel, livingEntity) :
                        originalModel.getOverrides().resolve(originalModel, itemStack, clientLevel, livingEntity);
            }
        };
    }

    @Override
    public ItemCameraTransforms getTransforms() {
        return originalModel.getTransforms();
    }

    private boolean isCheesed(ItemStack itemStack) {
        return HotpotTagsHelper.hasHotpotTag(itemStack) && HotpotTagsHelper.getHotpotTag(itemStack).contains("Cheesed", Constants.NBT.TAG_ANY_NUMERIC) && HotpotTagsHelper.getHotpotTag(itemStack).getBoolean("Cheesed");
    }
}
