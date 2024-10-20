package com.github.argon4w.hotpot.client.contents.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.api.client.contents.IHotpotContentRenderer;
import com.github.argon4w.hotpot.api.client.items.IHotpotStrainerBasketContentRenderer;
import com.github.argon4w.hotpot.api.contents.IHotpotContent;
import com.github.argon4w.hotpot.client.contents.HotpotStrainerBasketContentRenderers;
import com.github.argon4w.hotpot.contents.AbstractHotpotItemStackContent;
import com.github.argon4w.hotpot.contents.HotpotStrainerBasketContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HotpotStrainerBasketContentRenderer implements IHotpotContentRenderer {
    public static final double OFFSET = 1.0 / 16.0;
    public static final double SIZE = (14.0 / 3.0) / 16.0;
    public static final double CENTER = SIZE / 2.0;

    public static final double[] Z_BY_INDEX = {
            OFFSET + 2 * SIZE + CENTER,
            OFFSET + 2 * SIZE + CENTER,
            OFFSET + 1 * SIZE + CENTER,
            OFFSET + 0 * SIZE + CENTER,
            OFFSET + 0 * SIZE + CENTER,
            OFFSET + 0 * SIZE + CENTER,
            OFFSET + 1 * SIZE + CENTER,
            OFFSET + 2 * SIZE + CENTER
    };

    public static final double[] X_BY_INDEX = {
            OFFSET + 1 * SIZE + CENTER,
            OFFSET + 2 * SIZE + CENTER,
            OFFSET + 2 * SIZE + CENTER,
            OFFSET + 2 * SIZE + CENTER,
            OFFSET + 1 * SIZE + CENTER,
            OFFSET + 0 * SIZE + CENTER,
            OFFSET + 0 * SIZE + CENTER,
            OFFSET + 0 * SIZE + CENTER,
    };

    @Override
    public void render(IHotpotContent content, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, double rotation, double waterLevel, double x, double z, int index) {
        if (!(content instanceof HotpotStrainerBasketContent strainerBasket)) {
            return;
        }

        poseStack.pushPose();

        double time = rotation / 360.0;
        double offsetY = 0.02 + curve(time, Math.PI * index) * 0.01;

        poseStack.translate(X_BY_INDEX[index], 0.7875 + offsetY, Z_BY_INDEX[index]);
        poseStack.mulPose(Axis.YP.rotationDegrees(360 - strainerBasket.getDirection().toYRot()));
        poseStack.scale(0.45f, 0.45f, 0.45f);

        poseStack.pushPose();
        poseStack.translate(0, -0.5, 0);

        strainerBasket.getBasketContents().stream().filter(content1 -> content1 instanceof AbstractHotpotItemStackContent).map(content1 -> ((AbstractHotpotItemStackContent) content1).getItemStack()).collect(() -> new HashMap<IHotpotStrainerBasketContentRenderer, List<ItemStack>>(), (map, itemStack1) -> map.computeIfAbsent(HotpotStrainerBasketContentRenderers.getStrainerBasketContentRenderer(itemStack1), renderer -> new ArrayList<>()).add(itemStack1), (map1, map2) -> {}).forEach((renderer, itemStacks) -> renderer.renderInSoup(itemStacks, poseStack, bufferSource, combinedLight, combinedOverlay, index, waterLevel, (1 - 0.02 - offsetY / 0.45), time));
        poseStack.popPose();

        BakedModel model = Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "item/hotpot_strainer_basket_model")));
        Minecraft.getInstance().getItemRenderer().render(HotpotModEntry.HOTPOT_STRAINER_BASKET.toStack(), ItemDisplayContext.NONE, false, poseStack, bufferSource, combinedLight, combinedOverlay, model);

        poseStack.popPose();
    }

    public static double curve(double f, double offset) {
        return Math.sin((f + offset) / 0.25 * 2 * Math.PI);
    }
}
