package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.effects.HotpotEffectHelper;
import com.github.argon4w.hotpot.soups.renderers.HotpotBubbleRenderer;
import com.github.argon4w.hotpot.soups.renderers.IHotpotSoupCustomElementRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HotpotCheeseSoup extends AbstractEffectiveFluidBasedSoup {
    public static final HotpotBubbleRenderer HOTPOT_BUBBLE_RENDERER = new HotpotBubbleRenderer(0.35f, 0.8f, 55, new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_cheese_soup_bubble"));

    public HotpotCheeseSoup(){
        super(Map.of(
                (itemStack) -> itemStack.is(HotpotSoups.MILK_ITEM_TAG), new HotpotFluidRefill(1f, SoundEvents.BUCKET_EMPTY, () -> new ItemStack(Items.BUCKET)),
                itemStack -> itemStack.is(HotpotSoups.MILK_BOTTLE_ITEM_TAG), new HotpotFluidRefill(1f, SoundEvents.BOTTLE_FILL, () -> new ItemStack(Items.GLASS_BOTTLE))
        ));
    }

    @Override
    public String getID() {
        return "cheese_soup";
    }

    @Override
    public void animateTick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, RandomSource randomSource) {

    }

    @Override
    public ItemStack takeOutContentViaChopstick(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        ItemStack result = super.takeOutContentViaChopstick(content, itemStack, hotpotBlockEntity, pos);
        HotpotTagsHelper.updateHotpotTag(itemStack, compoundTag -> compoundTag.putBoolean("Cheesed", true));

        return result;
    }

    @Override
    public void addEffectToItem(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        HotpotEffectHelper.saveEffects(itemStack, new MobEffectInstance(HotpotModEntry.HOTPOT_WARM.get(), 15 * 20, 0));
        HotpotEffectHelper.saveEffects(itemStack, new MobEffectInstance(MobEffects.ABSORPTION, 20 * 20, 2));
        HotpotEffectHelper.saveEffects(itemStack, new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 20, 2));
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
        return List.of(HOTPOT_BUBBLE_RENDERER);
    }

    @Override
    public float getWaterLevelDropRate() {
        return 0.03f;
    }
}

