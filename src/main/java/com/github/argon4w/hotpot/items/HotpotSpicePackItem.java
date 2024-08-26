package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotMobEffectMap;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.AbstractHotpotItemStackContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.items.components.HotpotFoodEffectsDataComponent;
import com.github.argon4w.hotpot.items.components.HotpotSpicePackDataComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;

public class HotpotSpicePackItem extends Item implements IHotpotUpdateAwareContentItem {
    public HotpotSpicePackItem() {
        super(new Properties().component(HotpotModEntry.HOTPOT_SPICE_PACK_DATA_COMPONENT, HotpotSpicePackDataComponent.EMPTY));
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return hasSpicePackCharges(itemStack);
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        return Math.round(((float) getSpicePackCharges(itemStack) * 13.0F) / 20f);
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        float f = Math.max(0.0F, (float) getSpicePackCharges(itemStack) / 20f);
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public ItemStack onContentUpdate(ItemStack itemStack, IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        if (!hasSpicePackCharges(itemStack)) {
            return itemStack;
        }

        if (isSpicePackEmpty(itemStack)) {
            return itemStack;
        }

        if (!(content instanceof AbstractHotpotItemStackContent itemStackContent)) {
            return itemStack;
        }

        decreaseSpicePackCharges(itemStack);
        itemStackContent.updateItemStack(contentItemStack -> HotpotFoodEffectsDataComponent.addEffects(contentItemStack, getSpicePackEffects(itemStack)));

        return itemStack;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(itemStack, context, components, flag);

        if (isSpicePackEmpty(itemStack)) {
            return;
        }

        if (!hasSpicePackCharges(itemStack)) {
            return;
        }

        components.add(Component.translatable("item.everyxhotpot.hotpot_spice_pack.amount", getSpicePackCharges(itemStack)).withStyle(ChatFormatting.BLUE));
        PotionContents.addPotionTooltip(getSpicePackEffects(itemStack).getMobEffects(), components::add, 1.0f, context.tickRate());
    }

    public static HotpotSpicePackDataComponent getDataComponent(ItemStack itemStack) {
        return itemStack.getOrDefault(HotpotModEntry.HOTPOT_SPICE_PACK_DATA_COMPONENT, HotpotSpicePackDataComponent.EMPTY);
    }

    public static void setDataComponent(ItemStack itemStack, HotpotSpicePackDataComponent dataComponent) {
        itemStack.set(HotpotModEntry.HOTPOT_SPICE_PACK_DATA_COMPONENT, dataComponent);
    }

    public static List<ItemStack> getSpicePackItems(ItemStack itemStack) {
        return List.copyOf(getDataComponent(itemStack).itemStacks());
    }

    public static int getSpicePackCharges(ItemStack itemStack) {
        return getDataComponent(itemStack).charges();
    }

    public static boolean hasSpicePackCharges(ItemStack itemStack) {
        return getSpicePackCharges(itemStack) > 0;
    }

    public static boolean isSpicePackEmpty(ItemStack itemStack) {
        return getSpicePackItems(itemStack).isEmpty();
    }

    public static void decreaseSpicePackCharges(ItemStack itemStack) {
        setSpicePackCharges(itemStack, Math.max(0, getSpicePackCharges(itemStack) - 1));
    }

    public static void addSpicePackItems(ItemStack itemStack, ItemStack added) {
        setDataComponent(itemStack, getDataComponent(itemStack).addItemStack(added));
    }

    public static void setSpicePackCharges(ItemStack itemStack, int charges) {
        setDataComponent(itemStack, getDataComponent(itemStack).setCharges(charges));
    }

    public static HotpotMobEffectMap getSpicePackEffects(ItemStack itemStack) {
        return getDataComponent(itemStack).getFoodEffects();
    }
}
