package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.SimpleItemSlot;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class HotpotPlacedChopstick implements IHotpotPlacement {
    private final int pos1;
    private final int pos2;
    private final Direction direction;
    private final SimpleItemSlot chopstickItemSlot;

    public HotpotPlacedChopstick(int pos, Direction direction) {
        this.pos1 = pos;
        this.pos2 = pos + HotpotPlacementSerializers.DIRECTION_TO_POS.get(direction);
        this.direction = direction;
        this.chopstickItemSlot = new SimpleItemSlot();
    }

    public HotpotPlacedChopstick(int pos1, int pos2, SimpleItemSlot chopstickItemSlot) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.chopstickItemSlot = chopstickItemSlot;
        this.direction = HotpotPlacementSerializers.POS_TO_DIRECTION.get(pos2 - pos1);
    }

    @Override
    public void interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container) {
        if (container.canBeRemoved()) {
            onRemove(container, selfPos);
        }
    }

    @Override
    public ItemStack getContent(Player player, InteractionHand hand, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container, boolean tableware) {
        return ItemStack.EMPTY;
    }

    @Override
    public void onRemove(IHotpotPlacementContainerBlockEntity container, LevelBlockPos pos) {
        chopstickItemSlot.dropItem(pos);
    }

    @Override
    public boolean shouldRemove(Player player, InteractionHand hand, ItemStack itemStack, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container) {
        return chopstickItemSlot.isEmpty() && container.canBeRemoved();
    }

    @Override
    public ItemStack getCloneItemStack(IHotpotPlacementContainerBlockEntity container, LevelBlockPos selfPos) {
        return chopstickItemSlot.getItemStack();
    }

    @Override
    public List<Integer> getPosList() {
        return List.of(pos1, pos2);
    }

    @Override
    public Holder<IHotpotPlacementSerializer<?>> getPlacementSerializerHolder() {
        return HotpotPlacementSerializers.PLACED_CHOPSTICK_SERIALIZER;
    }

    public void setChopstickItemSlot(ItemStack chopstickItemSlot) {
        this.chopstickItemSlot.set(chopstickItemSlot);
    }

    public int getPos1() {
        return pos1;
    }

    public int getPos2() {
        return pos2;
    }

    public Direction getDirection() {
        return direction;
    }

    public SimpleItemSlot getChopstickItemSlot() {
        return chopstickItemSlot;
    }

    public static class Serializer implements IHotpotPlacementSerializer<HotpotPlacedChopstick> {
        public static final MapCodec<HotpotPlacedChopstick> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(chopstick -> chopstick.group(
                        Codec.INT.fieldOf("pos_1").forGetter(HotpotPlacedChopstick::getPos1),
                        Codec.INT.fieldOf("pos_2").forGetter(HotpotPlacedChopstick::getPos2),
                        SimpleItemSlot.CODEC.fieldOf("chopstick_item_slot").forGetter(HotpotPlacedChopstick::getChopstickItemSlot)
                ).apply(chopstick, HotpotPlacedChopstick::new))
        );

        @Override
        public HotpotPlacedChopstick get(int pos, Direction direction) {
            return new HotpotPlacedChopstick(pos, direction);
        }

        @Override
        public MapCodec<HotpotPlacedChopstick> getCodec() {
            return CODEC;
        }

        @Override
        public boolean canPlace(int pos, Direction direction) {
            return isValidPos(pos, pos + HotpotPlacementSerializers.DIRECTION_TO_POS.get(direction));
        }

        public boolean isValidPos(int pos1, int pos2) {
            return 0 <= pos1 && pos1 <= 3 && 0 <= pos2 && pos2 <= 3 && pos1 + pos2 != 3;
        }
    }
}
