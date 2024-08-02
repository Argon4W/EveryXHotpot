package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.github.argon4w.hotpot.items.components.HotpotNapkinHolderDataComponent;
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

public class HotpotPlacedNapkinHolder implements IHotpotPlate {
    private final int pos;
    private final int directionPos;
    private final Direction direction;
    private final SimpleItemSlot itemSlot;
    private final SimpleItemSlot napkinHolderItemSlot;

    public HotpotPlacedNapkinHolder(int pos, Direction direction) {
        this.pos = pos;
        this.direction = direction;
        this.itemSlot = new SimpleItemSlot();
        this.napkinHolderItemSlot = new SimpleItemSlot();
        this.directionPos = pos + HotpotPlacements.DIRECTION_TO_POS.get(direction);
    }

    public HotpotPlacedNapkinHolder(int pos, int directionPos, SimpleItemSlot itemSlot, SimpleItemSlot napkinHolderItemSlot) {
        this.pos = pos;
        this.directionPos = directionPos;
        this.itemSlot = itemSlot;
        this.napkinHolderItemSlot = napkinHolderItemSlot;
        this.direction = HotpotPlacements.POS_TO_DIRECTION.get(directionPos - pos);
    }

    @Override
    public boolean interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container) {
        if (itemStack.is(Items.PAPER)) {
            itemSlot.addItem(itemStack);
            return false;
        }

        if (player.isCrouching()) {
            return true;
        }

        if (!itemSlot.getItemStack().isEmpty() && itemSlot.getItemStack().is(Items.PAPER)) {
            itemSlot.getItemStack().shrink(1);
            container.markDataChanged();

            List<Holder<MobEffect>> holders = player.getActiveEffectsMap().keySet().stream().toList();
            player.removeEffect(holders.get(player.getRandom().nextInt(holders.size())));
        }

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
        return HotpotNapkinHolderDataComponent.setNapkin(napkinHolderItemSlot.getItemStack(), itemSlot.getItemStack());
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

    @Override
    public void setPlateItemSlot(ItemStack itemStack) {
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

    public SimpleItemSlot getItemSlot() {
        return itemSlot;
    }

    public SimpleItemSlot getNapkinHolderItemSlot() {
        return napkinHolderItemSlot;
    }

    public static class Factory implements IHotpotPlacementFactory<HotpotPlacedNapkinHolder> {
        public static final MapCodec<HotpotPlacedNapkinHolder> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(plate -> plate.group(
                        Codec.INT.fieldOf("Pos").forGetter(HotpotPlacedNapkinHolder::getPos),
                        Codec.INT.fieldOf("DirectionPos").forGetter(HotpotPlacedNapkinHolder::getDirectionPos),
                        SimpleItemSlot.CODEC.fieldOf("ItemSlot").forGetter(HotpotPlacedNapkinHolder::getItemSlot),
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
