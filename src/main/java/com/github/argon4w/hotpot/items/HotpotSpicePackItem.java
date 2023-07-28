package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.effects.HotpotEffectHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
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

        if (!isSpiceTagValid(selfItemStack)) {
            return itemStack;
        }

        int amount = getSpiceAmount(selfItemStack);

        if (amount <= 0) {
            return itemStack;
        }

        selfItemStack.getTag().putInt("Amount", Math.max(0, amount - 1));
        getSpiceEffects(selfItemStack).forEach(mobEffectInstance -> HotpotEffectHelper.saveEffects(itemStack, mobEffectInstance));

        return itemStack;
    }

    @Override
    public ItemStack getSelfItemStack(ItemStack selfItemStack, IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return isSpiceTagValid(selfItemStack) && getSpiceAmount(selfItemStack) <= 0 ? new ItemStack(HotpotModEntry.HOTPOT_SPICE_PACK.get()) : selfItemStack;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> tooltips, TooltipFlag flag) {
        super.appendHoverText(itemStack, level, tooltips, flag);

        if (isSpiceTagValid(itemStack)) {
            tooltips.add(Component.translatable("item.everyxhotpot.hotpot_spice_pack.amount", getSpiceAmount(itemStack)).withStyle(ChatFormatting.BLUE));
            PotionUtils.addPotionTooltip(HotpotEffectHelper.mergeEffects(getSpiceEffects(itemStack)), tooltips, 1.0F);
        }
    }

    private int getSpiceAmount(ItemStack itemStack) {
        return itemStack.getTag().getInt("Amount");
    }

    private boolean isSpiceTagValid(ItemStack itemStack) {
        return itemStack.hasTag() && itemStack.getTag().contains("Spices", Tag.TAG_LIST) && itemStack.getTag().contains("Amount", Tag.TAG_ANY_NUMERIC);
    }

    private List<MobEffectInstance> getSpiceEffects(ItemStack itemStack) {
        return itemStack.getTag().getList("Spices", Tag.TAG_COMPOUND).stream()
                .map(tag -> ItemStack.of((CompoundTag) tag))
                .map(itemStack1 -> SuspiciousEffectHolder.tryGet(itemStack1.getItem()))
                .filter(Objects::nonNull)
                .map(suspiciousEffectHolder -> new MobEffectInstance(suspiciousEffectHolder.getSuspiciousEffect(), suspiciousEffectHolder.getEffectDuration() * 2, 1))
                .toList();
    }
}
