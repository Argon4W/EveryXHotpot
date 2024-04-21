package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.effects.HotpotEffectHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SuspiciousEffectHolder;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HotpotSpicePackItem extends Item implements IHotpotSpecialContentItem {
    public HotpotSpicePackItem() {
        super(new Properties());
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return HotpotModEntry.HOTPOT_SPECIAL_ITEM_RENDERER;
            }
        });
    }

    @Override
    public ItemStack onOtherContentUpdate(ItemStack self, ItemStack itemStack, IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        if (itemStack.is(HotpotModEntry.HOTPOT_SPICE_PACK.get())) {
            return itemStack;
        }

        if (!isSpicePackValid(self)) {
            return itemStack;
        }

        int charges = getSpiceCharges(self);

        if (charges <= 0) {
            return itemStack;
        }

        setSpiceCharges(self, charges - 1);
        HotpotEffectHelper.saveEffects(itemStack, getSpiceEffects(self));

        return itemStack;
    }

    @Override
    public ItemStack updateSelf(ItemStack self, IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        if (!isSpicePackValid(self)) {
            return self;
        }

        if (getSpiceCharges(self) > 0) {
            return self;
        }

        return new ItemStack(HotpotModEntry.HOTPOT_SPICE_PACK.get());
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> tooltips, TooltipFlag flag) {
        super.appendHoverText(itemStack, level, tooltips, flag);

        if (isSpicePackValid(itemStack)) {
            tooltips.add(Component.translatable("item.everyxhotpot.hotpot_spice_pack.amount", getSpiceCharges(itemStack)).withStyle(ChatFormatting.BLUE));
            PotionUtils.addPotionTooltip(HotpotEffectHelper.mergeEffects(getSpiceEffects(itemStack)), tooltips, 1.0F);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return HotpotTagsHelper.hasHotpotTags(itemStack) && HotpotTagsHelper.getHotpotTags(itemStack).contains("SpicePackCharges");
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        return Math.round(((float) getSpiceCharges(itemStack) * 13.0F) / 20f);
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        float f = Math.max(0.0F, (float) getSpiceCharges(itemStack) / 20f);
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    public static void setSpiceCharges(ItemStack itemStack, int amount) {
        HotpotTagsHelper.updateHotpotTags(itemStack, "SpicePackCharges", IntTag.valueOf(amount));
    }

    public static void addSpicePackItems(ItemStack itemStack, ItemStack added) {
        if (!itemStack.is(HotpotModEntry.HOTPOT_SPICE_PACK.get())) {
            return;
        }

        if (added.isEmpty()) {
            return;
        }

        ArrayList<ItemStack> itemStacks = new ArrayList<>(getSpicePackItems(itemStack));
        itemStacks.add(added);
        setSpicePackItems(itemStack, itemStacks);
    }

    public static void setSpicePackItems(ItemStack itemStack, List<ItemStack> itemStacks) {
        HotpotTagsHelper.updateHotpotTags(itemStack, "SpicePackItems", itemStacks.stream()
                .filter(item -> !item.isEmpty())
                .map(HotpotTagsHelper::saveItemStack)
                .collect(Collectors.toCollection(ListTag::new)));
    }

    public static int getSpiceCharges(ItemStack itemStack) {
        if (!itemStack.is(HotpotModEntry.HOTPOT_SPICE_PACK.get())) {
            return 0;
        }

        if (!HotpotTagsHelper.hasHotpotTags(itemStack)) {
            return 0;
        }

        if (!HotpotTagsHelper.getHotpotTags(itemStack).contains("SpicePackCharges", Tag.TAG_ANY_NUMERIC)) {
            return 0;
        }

        return HotpotTagsHelper.getHotpotTags(itemStack).getInt("SpicePackCharges");
    }

    public static List<ItemStack> getSpicePackItems(ItemStack itemStack) {
        if (!itemStack.is(HotpotModEntry.HOTPOT_SPICE_PACK.get())) {
            return List.of();
        }

        if (!HotpotTagsHelper.hasHotpotTags(itemStack)) {
            return List.of();
        }

        if (!HotpotTagsHelper.getHotpotTags(itemStack).contains("SpicePackItems", Tag.TAG_LIST)) {
            return List.of();
        }

        return HotpotTagsHelper.getHotpotTags(itemStack).getList("SpicePackItems", Tag.TAG_COMPOUND).stream()
                .map(tag -> (CompoundTag) tag)
                .map(ItemStack::of)
                .filter(item -> !item.isEmpty())
                .toList();
    }

    public static List<MobEffectInstance> getSpiceEffects(ItemStack itemStack) {
        List<ItemStack> spicePackItems = getSpicePackItems(itemStack);

        if (spicePackItems.isEmpty()) {
            return List.of();
        }

        return spicePackItems.stream()
                .map(ItemStack::getItem)
                .map(SuspiciousEffectHolder::tryGet)
                .filter(Objects::nonNull)
                .map(suspiciousEffectHolder -> new MobEffectInstance(suspiciousEffectHolder.getSuspiciousEffect(), suspiciousEffectHolder.getEffectDuration() * 2, 1))
                .toList();
    }

    public static boolean isSpicePackValid(ItemStack itemStack) {
        if (!itemStack.is(HotpotModEntry.HOTPOT_SPICE_PACK.get())) {
            return false;
        }

        if (!HotpotTagsHelper.hasHotpotTags(itemStack)) {
            return false;
        }

        if (!HotpotTagsHelper.getHotpotTags(itemStack).contains("SpicePackItems", Tag.TAG_LIST)) {
            return false;
        }

        if (!HotpotTagsHelper.getHotpotTags(itemStack).contains("SpicePackCharges", Tag.TAG_ANY_NUMERIC)) {
            return false;
        }

        return true;
    }
}
