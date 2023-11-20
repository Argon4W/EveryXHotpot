package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.effects.HotpotEffectHelper;
import com.github.argon4w.hotpot.soups.renderers.HotpotBubbleRenderer;
import com.github.argon4w.hotpot.soups.renderers.IHotpotSoupCustomElementRenderer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class HotpotCheeseSoup extends AbstractEffectiveFluidBasedSoup {
    public static final HotpotBubbleRenderer HOTPOT_BUBBLE_RENDERER = new HotpotBubbleRenderer(0.35f, 0.8f, 55, new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_cheese_soup_bubble"));

    public HotpotCheeseSoup(){
        super(ImmutableMap.of(
                itemStack -> HotpotSoups.hasTag(itemStack, HotpotSoups.MILK_ITEM_TAG), new HotpotFluidRefill(1f, SoundEvents.BUCKET_EMPTY, () -> new ItemStack(Items.BUCKET)),
                itemStack -> HotpotSoups.hasTag(itemStack, HotpotSoups.MILK_BOTTLE_ITEM_TAG), new HotpotFluidRefill(1f, SoundEvents.BOTTLE_FILL, () -> new ItemStack(Items.GLASS_BOTTLE))
        ));
    }

    @Override
    public String getID() {
        return "CheeseSoup";
    }

    @Override
    public void animateTick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, Random randomSource) {

    }

    @Override
    public ItemStack takeOutContentViaChopstick(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        ItemStack result = super.takeOutContentViaChopstick(content, itemStack, hotpotBlockEntity, pos);
        HotpotTagsHelper.updateHotpotTag(itemStack, compoundTag -> compoundTag.putBoolean("Cheesed", true));

        return result;
    }

    @Override
    public void addEffectToItem(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        HotpotEffectHelper.saveEffects(itemStack, new EffectInstance(HotpotModEntry.HOTPOT_WARM.get(), 15 * 20, 0));
        HotpotEffectHelper.saveEffects(itemStack, new EffectInstance(Effects.ABSORPTION, 20 * 20, 2));
        HotpotEffectHelper.saveEffects(itemStack, new EffectInstance(Effects.DAMAGE_BOOST, 20 * 20, 2));
    }

    @Override
    public Optional<ResourceLocation> getBubbleResourceLocation() {
        return Optional.of(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_cheese_soup_bubble"));
    }

    @Override
    public Optional<ResourceLocation> getSoupResourceLocation() {
        return Optional.of(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_cheese_soup"));
    }

    @Override
    public List<IHotpotSoupCustomElementRenderer> getCustomElementRenderers() {
        return ImmutableList.of(HOTPOT_BUBBLE_RENDERER);
    }

    @Override
    public float getWaterLevelDropRate() {
        return 0.03f;
    }
}

