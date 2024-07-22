package com.github.argon4w.hotpot.client.items.process;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.items.process.processors.HotpotEmptySpriteProcessor;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ProcessedItemModelGenerator extends ItemModelGenerator {
    private final IHotpotSpriteProcessor processor;

    public ProcessedItemModelGenerator(IHotpotSpriteProcessor processor) {
        this.processor = processor;
    }

    @Override
    public BlockModel generateBlockModel(Function<Material, TextureAtlasSprite> spriteGetter, BlockModel model) {
        Map<String, Either<Material, String>> textureLayers = Maps.newHashMap();
        List<BlockElement> elements = Lists.newArrayList();

        for(int i = 0; i < LAYERS.size(); ++i) {
            String layer = LAYERS.get(i);
            String processedLayer = "hotpot_processed_" + layer;

            if (!model.hasTexture(layer)) {
                break;
            }

            Material material = model.getMaterial(layer);
            SpriteContents contents = spriteGetter.apply(material).contents();

            if (processor instanceof HotpotEmptySpriteProcessor) {
                continue;
            }

            Material processedMaterial = new Material(
                    material.atlasLocation(),
                    material.texture().withSuffix(processor.getProcessedSuffix())
            );

            SpriteContents processedContents = spriteGetter.apply(processedMaterial).contents();
            boolean processed = !processedContents.name().equals(MissingTextureAtlasSprite.getLocation());

            processedContents = processed ? processedContents : contents;
            textureLayers.put(processedLayer, Either.left(processed ? processedMaterial : material));

            elements.addAll(this.processFrames(
                    i + HotpotModEntry.HOTPOT_SPRITE_TINT_INDEX + LAYERS.size() * processor.getIndex(),
                    processedLayer,
                    processedContents
            ));
        }

        textureLayers.put("particle", model.hasTexture("particle") ? Either.left(model.getMaterial("particle")) : textureLayers.get("layer0"));
        BlockModel blockModel = new BlockModel(null, elements, textureLayers, false, model.getGuiLight(), model.getTransforms(), model.getOverrides());

        blockModel.name = model.name;
        blockModel.customData.copyFrom(model.customData);
        blockModel.customData.setGui3d(false);

        return blockModel;
    }
}
