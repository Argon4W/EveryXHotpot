package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.soups.synchronizers.IHotpotSoupSynchronizer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public interface IHotpotSoup {
    Optional<IHotpotContentSerializer<?>> interact(int hitPos, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos selfPos);
    Optional<IHotpotContentSerializer<?>> getContentSerializerFromItemStack(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    List<IHotpotSoupSynchronizer> getSynchronizers(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    ItemStack getContentViaTableware(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    void getContentViaHand(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    void onContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    void animateTick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, RandomSource randomSource);
    void setWaterLevel(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, float waterLevel);
    void discardOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    float getContentTickSpeed(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    void onEntityInside(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, Entity entity);
    void tick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    boolean isHotpotLit(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    HotpotSoupTypeHolder<?> getSoupTypeHolder();
    float getWaterLevel();
    float getOverflowWaterLevel();
}
