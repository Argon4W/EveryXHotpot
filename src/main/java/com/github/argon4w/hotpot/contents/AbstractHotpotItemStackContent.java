package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.items.IHotpotSpecialContentItem;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.github.argon4w.hotpot.soups.IHotpotSoupTypeWithActiveness;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Math;

import java.util.Optional;

public abstract class AbstractHotpotItemStackContent implements IHotpotContent {
    private ItemStack itemStack;
    private int cookingTime;
    private int cookingProgress;
    private float experience;

    public AbstractHotpotItemStackContent(ItemStack itemStack, int cookingTime, int cookingProgress, float experience) {
        this.itemStack = itemStack;
        this.cookingTime = cookingTime;
        this.cookingProgress = cookingProgress;
        this.experience = experience;
    }

    public AbstractHotpotItemStackContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity) {
        this.itemStack = itemStack.split(1);
        this.cookingTime = remapCookingTime(hotpotBlockEntity.getSoup(), itemStack, hotpotBlockEntity.getPos(), hotpotBlockEntity).orElse(-1);
        this.cookingProgress = 0;
        this.experience = 0;
    }

    public abstract Optional<Integer> remapCookingTime(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity);
    public abstract Optional<ItemStack> remapResult(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity);
    public abstract Optional<Float> remapExperience(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity);

    @Override
    public ItemStack takeOut(Player player, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        if (!(pos.level() instanceof ServerLevel serverLevel)) {
            return itemStack;
        }

        if (hotpotBlockEntity.getSoup() instanceof IHotpotSoupTypeWithActiveness withActiveness) {
            experience *= (1f + withActiveness.getActiveness());
        }

        ExperienceOrb.award(serverLevel, pos.toVec3(), Math.round(experience * 1.5f));

        return itemStack;
    }

    @Override
    public void onOtherContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        if (!(itemStack.getItem() instanceof IHotpotSpecialContentItem iHotpotSpecialContentItem)) {
            return;
        }

        if (!(content instanceof AbstractHotpotItemStackContent itemStackContent)) {
            return;
        }

        itemStackContent.itemStack = iHotpotSpecialContentItem.onOtherContentUpdate(itemStack, itemStackContent.itemStack, content, hotpotBlockEntity, pos);
        itemStack = iHotpotSpecialContentItem.updateSelf(itemStack, this, hotpotBlockEntity, pos);
    }

    @Override
    public boolean tick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        if (cookingTime < 0) {
            return false;
        }

        if (cookingProgress < cookingTime) {
            cookingProgress ++;
            return false;
        }

        Optional<ItemStack> resultOptional = remapResult(hotpotBlockEntity.getSoup(), itemStack, pos, hotpotBlockEntity);
        cookingTime = -1;

        if (resultOptional.isPresent()) {
            experience = remapExperience(hotpotBlockEntity.getSoup(), itemStack, pos, hotpotBlockEntity).orElse(0f);
            itemStack = resultOptional.get();

            return true;
        }

        return false;
    }

    @Override
    public boolean shouldRemove(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return false;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public int getCookingProgress() {
        return cookingProgress;
    }

    public float getExperience() {
        return experience;
    }

    public abstract static class Factory<T extends AbstractHotpotItemStackContent> implements IHotpotContentFactory<T> {
        public abstract T buildFromData(ItemStack itemStack, int cookingTime, int cookingProgress, float experience);

        @Override
        public MapCodec<T> buildFromCodec() {
            return RecordCodecBuilder.mapCodec(content -> content.group(
                    ItemStack.CODEC.fieldOf("ItemStack").forGetter(AbstractHotpotItemStackContent::getItemStack),
                    Codec.INT.fieldOf("CookingTime").forGetter(AbstractHotpotItemStackContent::getCookingTime),
                    Codec.INT.fieldOf("CookingProgress").forGetter(AbstractHotpotItemStackContent::getCookingProgress),
                    Codec.FLOAT.fieldOf("Experience").forGetter(AbstractHotpotItemStackContent::getExperience)
            ).apply(content, this::buildFromData));
        }
    }
}
