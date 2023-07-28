package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.IHotpotSavable;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.renderers.IHotpotSoupCustomElementRenderer;
import com.github.argon4w.hotpot.soups.synchronizers.IHotpotSoupSynchronizer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public interface IHotpotSoup extends IHotpotSavable<IHotpotSoup> {
    Optional<IHotpotContent> interact(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel selfPos);
    Optional<IHotpotContent> remapContent(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    Optional<IHotpotSoupSynchronizer> getSynchronizer(HotpotBlockEntity selfHotpotBlockEntity, BlockPosWithLevel selfPos);
    ItemStack takeOutContentViaChopstick(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    void takeOutContentViaHand(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    void contentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    void animateTick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, RandomSource randomSource);
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

    static IHotpotSoup loadSoup(CompoundTag compoundTag) {
        return isTagValid(compoundTag) ?
                HotpotSoups.getSoupOrElseEmpty(compoundTag.getString("Type")).get().loadOrElseGet(compoundTag, HotpotSoups.getEmptySoup())
                : HotpotSoups.getEmptySoup().get();
    }

    static boolean isTagValid(CompoundTag compoundTag) {
        return compoundTag.contains("Type", Tag.TAG_STRING);
    }

    static CompoundTag save(IHotpotSoup soup) {
        CompoundTag soupTag = new CompoundTag();
        soupTag.putString("Type", soup.getID());

        return soup.save(soupTag);
    }
}
