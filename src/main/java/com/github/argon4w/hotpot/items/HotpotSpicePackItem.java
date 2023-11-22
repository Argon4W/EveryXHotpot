package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.HotpotTagsHelper;
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
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HotpotSpicePackItem extends Item implements IHotpotSpecialContentItem {
    public HotpotSpicePackItem() {
        super(new Properties().tab(HotpotModEntry.HOTPOT_CREATIVE_TAB));
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

        setSpiceAmount(selfItemStack, amount - 1);
        getSpiceEffects(selfItemStack).forEach(mobEffectInstance -> HotpotEffectHelper.saveEffects(itemStack, mobEffectInstance));

        return itemStack;
    }

    @Override
    public ItemStack getSelfItemStack(ItemStack selfItemStack, IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return isSpiceTagValid(selfItemStack) && getSpiceAmount(selfItemStack) <= 0 ? new ItemStack(HotpotModEntry.HOTPOT_SPICE_PACK.get()) : selfItemStack;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> tooltips, TooltipFlag flag) {
        if (isSpiceTagValid(itemStack)) {
            tooltips.add(Component.translatable("item.everyxhotpot.hotpot_spice_pack.amount", getSpiceAmount(itemStack)).withStyle(ChatFormatting.BLUE));
            ItemStack copied = itemStack.copy();
            PotionUtils.setCustomEffects(copied, HotpotEffectHelper.mergeEffects(getSpiceEffects(itemStack)));

            PotionUtils.addPotionTooltip(copied, tooltips, 1.0F);
        }
    }

    public void setSpiceAmount(ItemStack itemStack, int amount) {
        HotpotTagsHelper.updateHotpotTag(itemStack, compoundTag -> compoundTag.putInt("SpiceAmount", amount));
    }

    private int getSpiceAmount(ItemStack itemStack) {
        return HotpotTagsHelper.getHotpotTag(itemStack).getInt("SpiceAmount");
    }

    private boolean isSpiceTagValid(ItemStack itemStack) {
        return HotpotTagsHelper.hasHotpotTag(itemStack) && HotpotTagsHelper.getHotpotTag(itemStack).contains("Spices", Tag.TAG_LIST) && HotpotTagsHelper.getHotpotTag(itemStack).contains("SpiceAmount", Tag.TAG_ANY_NUMERIC);
    }

    private List<MobEffectInstance> getSpiceEffects(ItemStack itemStack) {
        return HotpotTagsHelper.getHotpotTag(itemStack).getList("Spices", Tag.TAG_COMPOUND).stream()
                .map(tag -> ItemStack.of((CompoundTag) tag))
                .filter(itemStack1 -> itemStack1.getItem() instanceof BlockItem && ((BlockItem) itemStack1.getItem()).getBlock() instanceof FlowerBlock)
                .map(itemStack1 -> (FlowerBlock) ((BlockItem) itemStack1.getItem()).getBlock())
                .filter(Objects::nonNull)
                .map(flowerBlock -> new MobEffectInstance(flowerBlock.getSuspiciousStewEffect(), flowerBlock.getEffectDuration() * 2, 1))
                .collect(Collectors.toList());
    }
}
