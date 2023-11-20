package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.effects.HotpotEffectHelper;
import net.minecraft.block.FlowerBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HotpotSpicePackItem extends Item implements IHotpotSpecialContentItem {
    public HotpotSpicePackItem() {
        super(new Properties().setISTER(() -> HotpotBlockEntityWithoutLevelRenderer::new).tab(HotpotModEntry.HOTPOT_ITEM_GROUP));
    }

    @Override
    public ItemStack onOtherContentUpdate(ItemStack selfItemStack, ItemStack itemStack, IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        if (itemStack.getItem().equals(HotpotModEntry.HOTPOT_SPICE_PACK.get())) {
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
    public void appendHoverText(ItemStack itemStack, World level, List<ITextComponent> tooltips, ITooltipFlag flag) {
        if (isSpiceTagValid(itemStack)) {
            tooltips.add(new TranslationTextComponent("item.everyxhotpot.hotpot_spice_pack.amount", getSpiceAmount(itemStack)).withStyle(TextFormatting.BLUE));
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
        return HotpotTagsHelper.hasHotpotTag(itemStack) && HotpotTagsHelper.getHotpotTag(itemStack).contains("Spices", Constants.NBT.TAG_LIST) && HotpotTagsHelper.getHotpotTag(itemStack).contains("SpiceAmount", Constants.NBT.TAG_ANY_NUMERIC);
    }

    private List<EffectInstance> getSpiceEffects(ItemStack itemStack) {
        return HotpotTagsHelper.getHotpotTag(itemStack).getList("Spices", Constants.NBT.TAG_COMPOUND).stream()
                .map(tag -> ItemStack.of((CompoundNBT) tag))
                .filter(itemStack1 -> itemStack1.getItem() instanceof BlockItem && ((BlockItem) itemStack1.getItem()).getBlock() instanceof FlowerBlock)
                .map(itemStack1 -> (FlowerBlock) ((BlockItem) itemStack1.getItem()).getBlock())
                .filter(Objects::nonNull)
                .map(flowerBlock -> new EffectInstance(flowerBlock.getSuspiciousStewEffect(), flowerBlock.getEffectDuration() * 2, 1))
                .collect(Collectors.toList());
    }
}
