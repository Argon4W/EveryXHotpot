package com.github.argon4w.hotpot.api.placements;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.blocks.IHotpotPlacementContainer;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IHotpotPlacement {
    void interact(Player player, InteractionHand hand, ItemStack itemStack, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container);
    boolean shouldRemove(Player player, InteractionHand hand, ItemStack itemStack, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container);
    ItemStack getContent(Player player, InteractionHand hand, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container, boolean tableware);
    void onRemove(IHotpotPlacementContainer container, LevelBlockPos pos);
    ItemStack getCloneItemStack(IHotpotPlacementContainer container, LevelBlockPos pos);
    List<Integer> getPositions();
    Holder<IHotpotPlacementSerializer<?>> getPlacementSerializerHolder();
}
