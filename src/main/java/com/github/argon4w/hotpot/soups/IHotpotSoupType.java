package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.IHotpotSavable;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.synchronizers.IHotpotSoupSynchronizer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface IHotpotSoupType extends IHotpotSavable<IHotpotSoupType> {
    Optional<IHotpotContent> interact(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos selfPos);
    Optional<IHotpotContent> remapContent(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    Optional<IHotpotContent> remapItemStack(boolean copy, ItemStack itemStack, LevelBlockPos pos);
    Optional<IHotpotSoupSynchronizer> getSynchronizer(HotpotBlockEntity selfHotpotBlockEntity, LevelBlockPos selfPos);
    ItemStack takeOutContentViaTableware(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    void takeOutContentViaHand(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    void contentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    void animateTick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, RandomSource randomSource);
    float getWaterLevel(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    float getOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    void setWaterLevel(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, float waterLevel);
    void discardOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    int getContentTickSpeed(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    void entityInside(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, Entity entity);
    void tick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);

    static IHotpotSoupType loadSoup(CompoundTag compoundTag) {
        return isTagValid(compoundTag) ?
                HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER.buildSoup(new ResourceLocation(compoundTag.getString("id"))).loadOrElseGet(compoundTag, () -> HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER.buildEmptySoup())
                : HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER.buildEmptySoup();
    }

    static boolean isTagValid(CompoundTag compoundTag) {
        return compoundTag.contains("id", Tag.TAG_STRING);
    }

    static CompoundTag save(IHotpotSoupType soup) {
        CompoundTag soupTag = new CompoundTag();
        soupTag.putString("id", soup.getResourceLocation().toString());

        return soup.save(soupTag);
    }
}
