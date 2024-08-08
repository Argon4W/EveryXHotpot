package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.SimpleItemSlot;
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
    private final SimpleItemSlot napkinItemSlot;

    public HotpotPlacedNapkinHolder(int pos, Direction direction) {
        this.pos = pos;
        this.direction = direction;
        this.napkinHolderItemSlot = new SimpleItemSlot();
        this.napkinItemSlot = new SimpleItemSlot();
        this.directionPos = pos + HotpotPlacementSerializers.DIRECTION_TO_POS.get(direction);
    }

    public HotpotPlacedNapkinHolder(int pos, int directionPos, SimpleItemSlot napkinHolderItemSlot, SimpleItemSlot napkinItemSlot) {
        this.pos = pos;
        this.directionPos = directionPos;
        this.napkinHolderItemSlot = napkinHolderItemSlot;
        this.napkinItemSlot = napkinItemSlot;
        this.direction = HotpotPlacementSerializers.POS_TO_DIRECTION.get(directionPos - pos);
    }

    @Override
    public boolean interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container) {
        if (itemStack.is(Items.PAPER)) {
            napkinItemSlot.addItem(itemStack);
            return false;
        }

        if (player.isCrouching()) {
            return true;
        }

        if (napkinItemSlot.isEmpty()) {
            return false;
        }

        if (!napkinItemSlot.getItemStack().is(Items.PAPER)) {
            napkinItemSlot.dropItem(selfPos);
            return false;
        }

        napkinItemSlot.getItemStack().shrink(1);

        if (player.getActiveEffects().isEmpty()) {
            return false;
        }

        List<Holder<MobEffect>> holders = player.getActiveEffectsMap().keySet().stream().toList();
        player.removeEffect(holders.get(player.getRandom().nextInt(holders.size())));

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
        return HotpotNapkinHolderItem.setNapkinItemStack(napkinHolderItemSlot.getItemStack(), napkinItemSlot.getItemStack());
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
    public Holder<IHotpotPlacementSerializer<?>> getPlacementSerializerHolder() {
        return HotpotPlacementSerializers.NAPKIN_HOLDER_SERIALIZER;
    }

    public void setNapkinHolderItemSlot(ItemStack itemStack) {
        napkinHolderItemSlot.set(itemStack);
        napkinItemSlot.set(HotpotNapkinHolderItem.getNapkinItemStack(itemStack));
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
        return napkinItemSlot;
    }

    public static class Serializer implements IHotpotPlacementSerializer<HotpotPlacedNapkinHolder> {
        public static final MapCodec<HotpotPlacedNapkinHolder> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(plate -> plate.group(
                        Codec.INT.fieldOf("pos").forGetter(HotpotPlacedNapkinHolder::getPos),
                        Codec.INT.fieldOf("direction_pos").forGetter(HotpotPlacedNapkinHolder::getDirectionPos),
                        SimpleItemSlot.CODEC.fieldOf("napkin_holder_item_slot").forGetter(HotpotPlacedNapkinHolder::getNapkinHolderItemSlot),
                        SimpleItemSlot.CODEC.fieldOf("napkin_item_slot").forGetter(HotpotPlacedNapkinHolder::getNapkinItemSlot)
                ).apply(plate, HotpotPlacedNapkinHolder::new))
        );

        @Override
        public HotpotPlacedNapkinHolder get(int pos, Direction direction) {
            return new HotpotPlacedNapkinHolder(pos, direction);
        }

        @Override
        public MapCodec<HotpotPlacedNapkinHolder> getCodec() {
            return CODEC;
        }

        @Override
        public boolean canPlace(int pos, Direction direction) {
            return true;
        }
    }
}
