package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.effects.HotpotEffectHelper;
import com.github.argon4w.hotpot.soups.renderers.HotpotBubbleRenderer;
import com.github.argon4w.hotpot.soups.renderers.IHotpotSoupCustomElementRenderer;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class HotpotClearSoup extends AbstractHotpotWaterBasedSoup {
    public static final HotpotBubbleRenderer HOTPOT_BUBBLE_RENDERER = new HotpotBubbleRenderer(0.35f, 0.6f, 50, new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_clear_soup_bubble"));

    @Override
    public String getID() {
        return "ClearSoup";
    }

    @Override
    public void animateTick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, Random randomSource) {

    }

    @Override
    public void addEffectToItem(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        HotpotEffectHelper.saveEffects(itemStack, new EffectInstance(HotpotModEntry.HOTPOT_WARM.get(), 15 * 20, 0));
    }

    @Override
    public Optional<ResourceLocation> getBubbleResourceLocation() {
        return Optional.of(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_clear_soup_bubble"));
    }

    @Override
    public Optional<ResourceLocation> getSoupResourceLocation() {
        return Optional.of(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_clear_soup"));
    }

    @Override
    public List<IHotpotSoupCustomElementRenderer> getCustomElementRenderers() {
        return ImmutableList.of(HOTPOT_BUBBLE_RENDERER);
    }
}
