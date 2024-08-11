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

public class HotpotPlacedSpoon implements IHotpotPlacement {
    private final int pos1;
    public final int pos2;
    private final Direction direction;
    private final SimpleItemSlot spoonItemSlot;

    public HotpotPlacedSpoon(int pos, Direction direction) {
        this.pos1 = pos;
        this.pos2 = pos + HotpotPlacementSerializers.DIRECTION_TO_POS.get(direction);
        this.direction = direction;
        this.spoonItemSlot = new SimpleItemSlot();
    }

    public HotpotPlacedSpoon(int pos1, int pos2, SimpleItemSlot spoonItemSlot) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.spoonItemSlot = spoonItemSlot;
        this.direction = HotpotPlacementSerializers.POS_TO_DIRECTION.get(pos2 - pos1);
    }

    @Override
    public void interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container) {
        onRemove(container, selfPos);
    }

    @Override
    public ItemStack getContent(Player player, InteractionHand hand, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container, boolean tableware) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean shouldRemove(Player player, InteractionHand hand, ItemStack itemStack, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container) {
        return spoonItemSlot.isEmpty() && container.canBeRemoved();
    }

    @Override
    public void onRemove(IHotpotPlacementContainerBlockEntity container, LevelBlockPos pos) {
        spoonItemSlot.dropItem(pos);
    }

    @Override
    public ItemStack getCloneItemStack(IHotpotPlacementContainerBlockEntity container, LevelBlockPos selfPos) {
        return spoonItemSlot.getItemStack();
    }

    @Override
    public List<Integer> getPosList() {
        return List.of(pos1, pos2);
    }

    @Override
    public Holder<IHotpotPlacementSerializer<?>> getPlacementSerializerHolder() {
        return HotpotPlacementSerializers.PLACED_SPOON_SERIALIZER;
    }

    public void setSpoonItemSlot(ItemStack spoonItemSlot) {
        this.spoonItemSlot.set(spoonItemSlot);
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

    public SimpleItemSlot getSpoonItemSlot() {
        return spoonItemSlot;
    }

    public static class Serializer implements IHotpotPlacementSerializer<HotpotPlacedSpoon> {
        public static final MapCodec<HotpotPlacedSpoon> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(spoon -> spoon.group(
                        Codec.INT.fieldOf("pos_1").forGetter(HotpotPlacedSpoon::getPos1),
                        Codec.INT.fieldOf("pos_2").forGetter(HotpotPlacedSpoon::getPos2),
                        SimpleItemSlot.CODEC.fieldOf("spoon_item_slot").forGetter(HotpotPlacedSpoon::getSpoonItemSlot)
                ).apply(spoon, HotpotPlacedSpoon::new))
        );

        @Override
        public HotpotPlacedSpoon get(int pos, Direction direction) {
            return new HotpotPlacedSpoon(pos, direction);
        }

        @Override
        public MapCodec<HotpotPlacedSpoon> getCodec() {
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
