package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotItemStackContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.spice.SpiceEffectHelper;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SuspiciousEffectHolder;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class HotpotSpicePackItem extends Item implements IHotpotSpecialContentItem {
    public HotpotSpicePackItem() {
        super(new Properties());
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return HotpotModEntry.HOTPOT_BEWLR;
            }
        });
    }

    @Override
    public ItemStack onOtherContentUpdate(ItemStack selfItemStack, ItemStack itemStack, IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        if (itemStack.is(HotpotModEntry.HOTPOT_SPICE_PACK.get())) {
            return itemStack;
        }

        if (!selfItemStack.hasTag()) {
            return itemStack;
        }

        int amount = selfItemStack.getTag().getInt("Amount");

        if (amount <= 0) {
            return itemStack;
        }

        selfItemStack.getTag().putInt("Amount", Math.max(0, amount - 1));

        selfItemStack.getTag().getList("Spices", Tag.TAG_COMPOUND).stream()
                .map(tag -> ItemStack.of((CompoundTag) tag))
                .map(itemStack1 -> SuspiciousEffectHolder.tryGet(itemStack1.getItem()))
                .filter(Objects::nonNull)
                .map(suspiciousEffectHolder -> new MobEffectInstance(suspiciousEffectHolder.getSuspiciousEffect(), suspiciousEffectHolder.getEffectDuration() * 2, 1))
                .forEach(mobEffectInstance -> SpiceEffectHelper.saveEffects(itemStack, mobEffectInstance));
        return itemStack;
    }

    @Override
    public ItemStack getSelfItemStack(ItemStack selfItemStack, IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        if (!selfItemStack.hasTag()) return selfItemStack;

        int amount = selfItemStack.getOrCreateTag().getInt("Amount");

        return amount <= 0 ? new ItemStack(HotpotModEntry.HOTPOT_SPICE_PACK.get()) : selfItemStack;
    }
}
