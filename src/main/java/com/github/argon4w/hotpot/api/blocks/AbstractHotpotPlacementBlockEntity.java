package com.github.argon4w.hotpot.api.blocks;

import com.github.argon4w.fancytoys.AbstractCodecBlockEntity;
import com.github.argon4w.fancytoys.LevelBlockPos;
import com.github.argon4w.hotpot.api.placements.IHotpotPlacement;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractHotpotPlacementBlockEntity<T, P extends AbstractCodecBlockEntity.PartialData<T>>
        extends AbstractCodecBlockEntity<T, P>
        implements IHotpotPlacementContainer {

    public AbstractHotpotPlacementBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public abstract int getPlacementIndexInPosAndLayer(int position, int layer);
    public abstract List<IHotpotPlacement> getPlacements(int layer);
    public abstract void removePlacement(int index, int layer, LevelBlockPos pos);
    public abstract void markDataChanged();
    public abstract boolean shouldRemove();

    @Override
    public ItemStack getContentByTableware(Context context) {
        int position = context.position();
        int layer = context.layer();
        int index = getPlacementIndexInPosAndLayer(position, layer);

        Player player = context.player();
        InteractionHand hand = context.hand();
        LevelBlockPos pos = context.pos();

        if (index < 0) {
            return ItemStack.EMPTY;
        }

        IHotpotPlacement placement = getPlacements(layer).get(index);
        ItemStack itemStack = placement.getContent(player, hand, position, layer, pos, this, true);

        if (placement.shouldRemove(player, hand, itemStack, position, layer, pos, this)) {
            removePlacement(index, layer, pos);
        }

        if (shouldRemove()) {
            context.pos().removeBlock(true);
        }

        markDataChanged();
        return itemStack;
    }

    @Override
    public void setContentByInteraction(Context context, ItemStack itemStack) {
        int position = context.position();
        int layer = context.layer();
        int index = getPlacementIndexInPosAndLayer(position, layer);

        Player player = context.player();
        InteractionHand hand = context.hand();
        LevelBlockPos pos = context.pos();

        if (index < 0) {
            return;
        }

        IHotpotPlacement placement = getPlacements(layer).get(index);
        placement.interact(player, hand, itemStack, position, layer, pos, this);

        if (placement.shouldRemove(player, hand, itemStack, position, layer, pos, this)) {
            removePlacement(index, layer, pos);
        }

        if (shouldRemove()) {
            context.pos().removeBlock(true);
        }

        markDataChanged();
    }
}
