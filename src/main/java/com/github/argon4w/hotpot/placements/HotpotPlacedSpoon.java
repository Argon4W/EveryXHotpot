package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.LevelBlockPos;
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
        this.pos2 = pos + HotpotPlacements.DIRECTION_TO_POS.get(direction);
        this.direction = direction;
        this.spoonItemSlot = new SimpleItemSlot();
    }

    public HotpotPlacedSpoon(int pos1, int pos2, SimpleItemSlot spoonItemSlot) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.spoonItemSlot = spoonItemSlot;
        this.direction = HotpotPlacements.POS_TO_DIRECTION.get(pos2 - pos1);
    }

    @Override
    public boolean interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container) {
        return true;
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
        return spoonItemSlot.getItemStack();
    }

    @Override
    public List<Integer> getPoslist() {
        return List.of(pos1, pos2);
    }

    @Override
    public boolean isConflict(int pos) {
        return pos1 == pos || pos2 == pos;
    }

    @Override
    public Holder<IHotpotPlacementFactory<?>> getPlacementFactoryHolder() {
        return HotpotPlacements.PLACED_SPOON;
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

    public static class Factory implements IHotpotPlacementFactory<HotpotPlacedSpoon> {
        public static final MapCodec<HotpotPlacedSpoon> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(spoon -> spoon.group(
                        Codec.INT.fieldOf("Pos1").forGetter(HotpotPlacedSpoon::getPos1),
                        Codec.INT.fieldOf("Pos2").forGetter(HotpotPlacedSpoon::getPos2),
                        SimpleItemSlot.CODEC.fieldOf("Spoon").forGetter(HotpotPlacedSpoon::getSpoonItemSlot)
                ).apply(spoon, HotpotPlacedSpoon::new))
        );

        @Override
        public HotpotPlacedSpoon buildFromSlots(int pos, Direction direction) {
            return new HotpotPlacedSpoon(pos, direction);
        }

        @Override
        public MapCodec<HotpotPlacedSpoon> buildFromCodec() {
            return CODEC;
        }

        @Override
        public boolean canPlace(int pos, Direction direction) {
            return isValidPos(pos, pos + HotpotPlacements.DIRECTION_TO_POS.get(direction));
        }

        public boolean isValidPos(int pos1, int pos2) {
            return 0 <= pos1 && pos1 <= 3 && 0 <= pos2 && pos2 <= 3 && pos1 + pos2 != 3;
        }
    }
}
