package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.effects.HotpotEffectHelper;
import com.github.argon4w.hotpot.soups.renderers.HotpotSpicySoupBubbleRenderer;
import com.github.argon4w.hotpot.soups.renderers.HotpotSpicySoupFloatingPepperRenderer;
import com.github.argon4w.hotpot.soups.renderers.IHotpotSoupCustomElementRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class HotpotSpicySoup extends AbstractHotpotWaterBasedSoup {
    public static final HotpotSpicySoupBubbleRenderer HOTPOT_BUBBLE_RENDERER = new HotpotSpicySoupBubbleRenderer();
    public static final HotpotSpicySoupFloatingPepperRenderer HOTPOT_SPICY_SOUP_FLOATING_PEPPER_RENDERER = new HotpotSpicySoupFloatingPepperRenderer();

    @Override
    public String getID() {
        return "spicy_soup";
    }

    @Override
    public void animateTick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, RandomSource randomSource) {

    }

    @Override
    public void addEffectToItem(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        HotpotEffectHelper.saveEffects(itemStack, new MobEffectInstance(HotpotModEntry.HOTPOT_WARM.get(), 15 * 20, 0));
        HotpotEffectHelper.saveEffects(itemStack, new MobEffectInstance(HotpotModEntry.HOTPOT_ACRID.get(), 15 * 20, 1));
        HotpotEffectHelper.saveEffects(itemStack, new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10 * 20, 1));
    }

    @Override
    public Optional<ResourceLocation> getBubbleResourceLocation() {
        return Optional.of(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_spicy_soup_bubble_small"));
    }

    @Override
    public Optional<ResourceLocation> getSoupResourceLocation() {
        return Optional.of(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_spicy_soup"));
    }

    @Override
    public List<IHotpotSoupCustomElementRenderer> getCustomElementRenderers() {
        return List.of(HOTPOT_BUBBLE_RENDERER, HOTPOT_SPICY_SOUP_FLOATING_PEPPER_RENDERER);
    }
}
