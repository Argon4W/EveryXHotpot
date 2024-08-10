package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IHotpotPlacement {
    void interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container);
    boolean shouldRemove(Player player, InteractionHand hand, ItemStack itemStack, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container);
    ItemStack getContent(Player player, InteractionHand hand, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container, boolean tableware);
    void onRemove(IHotpotPlacementContainerBlockEntity container, LevelBlockPos pos);
    ItemStack getCloneItemStack(IHotpotPlacementContainerBlockEntity container, LevelBlockPos selfPos);
    List<Integer> getPosList();
    Holder<IHotpotPlacementSerializer<?>> getPlacementSerializerHolder();
}
