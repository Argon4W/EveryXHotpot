package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.IHotpotSavable;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.renderers.IHotpotSoupCustomElementRenderer;
import com.github.argon4w.hotpot.soups.synchronizers.IHotpotSoupSynchronizer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public interface IHotpotSoup extends IHotpotSavable<IHotpotSoup> {
    Optional<IHotpotContent> interact(int hitSection, PlayerEntity player, Hand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel selfPos);
    Optional<IHotpotContent> remapContent(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    Optional<IHotpotSoupSynchronizer> getSynchronizer(HotpotBlockEntity selfHotpotBlockEntity, BlockPosWithLevel selfPos);
    ItemStack takeOutContentViaChopstick(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    void takeOutContentViaHand(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    void contentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    void animateTick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, Random randomSource);
    float getWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    float getOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    void setWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, float waterLevel);
    void discardOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    int getContentTickSpeed(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    void entityInside(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, Entity entity);
    void tick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    Optional<ResourceLocation> getBubbleResourceLocation();
    Optional<ResourceLocation> getSoupResourceLocation();
    List<IHotpotSoupCustomElementRenderer> getCustomElementRenderers();

    static IHotpotSoup loadSoup(CompoundNBT compoundTag) {
        return isTagValid(compoundTag) ?
                HotpotSoups.getSoupOrElseEmpty(compoundTag.getString("Type")).get().loadOrElseGet(compoundTag, HotpotSoups.getEmptySoup())
                : HotpotSoups.getEmptySoup().get();
    }

    static boolean isTagValid(CompoundNBT compoundTag) {
        return compoundTag.contains("Type", Constants.NBT.TAG_STRING);
    }

    static CompoundNBT save(IHotpotSoup soup) {
        CompoundNBT soupTag = new CompoundNBT();
        soupTag.putString("Type", soup.getID());

        return soup.save(soupTag);
    }
}
