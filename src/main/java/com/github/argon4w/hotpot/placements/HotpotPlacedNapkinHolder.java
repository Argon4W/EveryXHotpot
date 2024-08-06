package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.github.argon4w.hotpot.items.HotpotNapkinHolderItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class HotpotPlacedNapkinHolder implements IHotpotPlacement {
    private final int pos;
    private final int directionPos;
    private final Direction direction;
    private final SimpleItemSlot napkinHolderItemSlot;

    public HotpotPlacedNapkinHolder(int pos, Direction direction) {
        this.pos = pos;
        this.direction = direction;
        this.napkinHolderItemSlot = new SimpleItemSlot();
        this.directionPos = pos + HotpotPlacements.DIRECTION_TO_POS.get(direction);
    }

    public HotpotPlacedNapkinHolder(int pos, int directionPos, SimpleItemSlot napkinHolderItemSlot) {
        this.pos = pos;
        this.directionPos = directionPos;
        this.napkinHolderItemSlot = napkinHolderItemSlot;
        this.direction = HotpotPlacements.POS_TO_DIRECTION.get(directionPos - pos);
    }

    @Override
    public boolean interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container) {
        if (itemStack.is(Items.PAPER)) {
            addNapkinHolderItemStack(itemStack);
            container.markDataChanged();
            return false;
        }

        if (player.isCrouching()) {
            return true;
        }

        if (isNapkinHolderEmpty()) {
            return false;
        }

        if (!isNapkinHolderItemStackPaper()) {
            dropNapkinHolderItemStack(selfPos);
            container.markDataChanged();
            return false;
        }

        shrinkNapkinHolderItemStack();
        container.markDataChanged();
        removeRandomEffect(player);

        return false;
    }

    @Override
    public ItemStack takeOutContent(int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container, boolean tableware) {
        return ItemStack.EMPTY;
    }

    @Override
    public void onRemove(IHotpotPlacementContainerBlockEntity container, LevelBlockPos pos) {

    }

    @Override
    public ItemStack getCloneItemStack(IHotpotPlacementContainerBlockEntity container, LevelBlockPos selfPos) {
        return napkinHolderItemSlot.getItemStack();
    }

    @Override
    public List<Integer> getPoslist() {
        return List.of(pos);
    }

    @Override
    public boolean isConflict(int pos) {
        return this.pos == pos;
    }

    @Override
    public Holder<IHotpotPlacementFactory<?>> getPlacementFactoryHolder() {
        return HotpotPlacements.NAPKIN_HOLDER;
    }

    public void setNapkinHolderItemSlot(ItemStack itemStack) {
        napkinHolderItemSlot.set(itemStack);
    }

    public int getPos() {
        return pos;
    }

    public int getDirectionPos() {
        return directionPos;
    }

    public Direction getDirection() {
        return direction;
    }

    public SimpleItemSlot getNapkinHolderItemSlot() {
        return napkinHolderItemSlot;
    }

    public SimpleItemSlot getNapkinItemSlot() {
        return HotpotNapkinHolderItem.getNapkinHolderItemSlot(napkinHolderItemSlot);
    }

    public boolean isNapkinHolderEmpty() {
        return HotpotNapkinHolderItem.isNapkinHolderEmpty(napkinHolderItemSlot);
    }

    public boolean isNapkinHolderItemStackPaper() {
        return getNapkinItemSlot().getItemStack().is(Items.PAPER);
    }

    public void addNapkinHolderItemStack(ItemStack itemStack) {
        HotpotNapkinHolderItem.addNapkinHolderItemStack(napkinHolderItemSlot, itemStack);
    }

    public void dropNapkinHolderItemStack(LevelBlockPos pos) {
        HotpotNapkinHolderItem.dropNapkinHolderItemStack(napkinHolderItemSlot, pos);
    }

    public void shrinkNapkinHolderItemStack() {
        HotpotNapkinHolderItem.shrinkNapkinHolderItemStack(napkinHolderItemSlot);
    }

    public void removeRandomEffect(Player player) {
        List<Holder<MobEffect>> holders = player.getActiveEffectsMap().keySet().stream().toList();
        player.removeEffect(holders.get(player.getRandom().nextInt(holders.size())));
    }

    public static class Factory implements IHotpotPlacementFactory<HotpotPlacedNapkinHolder> {
        public static final MapCodec<HotpotPlacedNapkinHolder> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(plate -> plate.group(
                        Codec.INT.fieldOf("Pos").forGetter(HotpotPlacedNapkinHolder::getPos),
                        Codec.INT.fieldOf("DirectionPos").forGetter(HotpotPlacedNapkinHolder::getDirectionPos),
                        SimpleItemSlot.CODEC.fieldOf("NapkinHolderItemSlot").forGetter(HotpotPlacedNapkinHolder::getNapkinHolderItemSlot)
                ).apply(plate, HotpotPlacedNapkinHolder::new))
        );

        @Override
        public HotpotPlacedNapkinHolder buildFromSlots(int pos, Direction direction) {
            return new HotpotPlacedNapkinHolder(pos, direction);
        }

        @Override
        public MapCodec<HotpotPlacedNapkinHolder> buildFromCodec() {
            return CODEC;
        }

        @Override
        public boolean canPlace(int pos, Direction direction) {
            return true;
        }
    }
}
