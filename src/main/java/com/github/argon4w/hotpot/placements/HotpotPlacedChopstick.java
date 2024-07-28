package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class HotpotPlacedChopstick implements IHotpotPlacement {
    private final int pos1;
    private final int pos2;
    private final Direction direction;
    private final SimpleItemSlot chopstickItemSlot;

    public HotpotPlacedChopstick(int pos, Direction direction) {
        this.pos1 = pos;
        this.pos2 = pos + HotpotPlacements.DIRECTION_TO_POS.get(direction);
        this.direction = direction;
        this.chopstickItemSlot = new SimpleItemSlot();
    }

    public HotpotPlacedChopstick(int pos1, int pos2, SimpleItemSlot chopstickItemSlot) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.chopstickItemSlot = chopstickItemSlot;
        this.direction = HotpotPlacements.POS_TO_DIRECTION.get(pos2 - pos1);
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "placed_chopstick");
    }

    @Override
    public boolean interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos selfPos) {
        return true;
    }

    @Override
    public ItemStack takeOutContent(int pos, HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos selfPos, boolean tableware) {
        return ItemStack.EMPTY;
    }

    @Override
    public void onRemove(HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos pos) {

    }

    @Override
    public ItemStack getCloneItemStack(HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos level) {
        return chopstickItemSlot.getItemStack();
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
    public IHotpotPlacementFactory<?> getFactory() {
        return HotpotPlacements.PLACED_CHOPSTICK.get();
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

    public static class Factory implements IHotpotPlacementFactory<HotpotPlacedChopstick> {
        public static final MapCodec<HotpotPlacedChopstick> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(chopstick -> chopstick.group(
                        Codec.INT.fieldOf("Pos1").forGetter(HotpotPlacedChopstick::getPos1),
                        Codec.INT.fieldOf("Pos2").forGetter(HotpotPlacedChopstick::getPos2),
                        SimpleItemSlot.CODEC.fieldOf("Chopstick").forGetter(HotpotPlacedChopstick::getChopstickItemSlot)
                ).apply(chopstick, HotpotPlacedChopstick::new))
        );

        @Override
        public HotpotPlacedChopstick buildFromSlots(int pos, Direction direction) {
            return new HotpotPlacedChopstick(pos, direction);
        }

        @Override
        public MapCodec<HotpotPlacedChopstick> buildFromCodec() {
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
