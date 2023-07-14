package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotDefinitions;
import com.github.argon4w.hotpot.IHotpotSavable;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface IHotpotSoup extends IHotpotSavable {
    Optional<IHotpotContent> interact(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel selfPos);
    Optional<IHotpotContent> remapContent(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    Optional<IHotpotSoupSynchronizer> getSynchronizer(HotpotBlockEntity selfHotpotBlockEntity, BlockPosWithLevel selfPos);
    void takeOutContent(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    float getWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    float getOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    void setWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, float waterLevel);
    void discardOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    int getContentTickSpeed(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    void entityInside(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, Entity entity);
    void tick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    Optional<ResourceLocation> getBubbleResourceLocation();
    Optional<ResourceLocation> getSoupResourceLocation();

    static IHotpotSoup loadSoup(CompoundTag compoundTag, IHotpotSoup original) {
        if (isSoupTagValid(compoundTag)) {
            CompoundTag soupTag = compoundTag.getCompound("Soup");

            String type = soupTag.getString("Type");
            IHotpotSoup soup = HotpotDefinitions.getSoupOrElseEmpty(type).get();

            return IHotpotSavable.loadOrElseGet(soup, soupTag, HotpotDefinitions.getEmptySoup());
        }

        return original;
    }

    static boolean isSoupTagValid(CompoundTag compoundTag) {
        return compoundTag.contains("Soup", Tag.TAG_COMPOUND) && compoundTag.getCompound("Soup").contains("Type", Tag.TAG_STRING);
    }

    static void saveSoup(CompoundTag compoundTag, IHotpotSoup soup) {
        CompoundTag soupTag = new CompoundTag();
        soupTag.putString("Type", soup.getID());
        compoundTag.put("Soup", soup.save(soupTag));
    }
}
