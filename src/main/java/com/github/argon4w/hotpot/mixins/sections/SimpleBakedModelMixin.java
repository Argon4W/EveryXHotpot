package com.github.argon4w.hotpot.mixins.sections;

import com.github.argon4w.hotpot.client.sections.ISimpleBakedModelExtension;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.neoforged.neoforge.client.RenderTypeGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(SimpleBakedModel.class)
public class SimpleBakedModelMixin implements ISimpleBakedModelExtension {
    @Unique
    private RenderTypeGroup everyxhotpot$renderTypeGroup;

    @Inject(method = "<init>(Ljava/util/List;Ljava/util/Map;ZZZLnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lnet/minecraft/client/renderer/block/model/ItemTransforms;Lnet/minecraft/client/renderer/block/model/ItemOverrides;Lnet/neoforged/neoforge/client/RenderTypeGroup;)V", at = @At("TAIL"))
    private void constructor(List pUnculledFaces, Map pCulledFaces, boolean pHasAmbientOcclusion, boolean pUsesBlockLight, boolean pIsGui3d, TextureAtlasSprite pParticleIcon, ItemTransforms pTransforms, ItemOverrides pOverrides, RenderTypeGroup renderTypes, CallbackInfo ci) {
        this.everyxhotpot$renderTypeGroup = renderTypes;
    }

    @Unique
    @Override
    public RenderTypeGroup everyxhotpot$getRenderTypeGroup() {
        return everyxhotpot$renderTypeGroup;
    }
}
