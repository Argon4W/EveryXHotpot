package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class HotpotLargeRoundPlate implements IHotpotPlacement {
    private final SimpleItemSlot itemSlot1;
    private final SimpleItemSlot itemSlot2;
    private final SimpleItemSlot itemSlot3;
    private final SimpleItemSlot itemSlot4;
    private final SimpleItemSlot[] slots;

    public HotpotLargeRoundPlate() {
        this.itemSlot1 = new SimpleItemSlot();
        this.itemSlot2 = new SimpleItemSlot();
        this.itemSlot3 = new SimpleItemSlot();
        this.itemSlot4 = new SimpleItemSlot();
        slots = new SimpleItemSlot[] {itemSlot1, itemSlot2, itemSlot3, itemSlot4};
    }

    public HotpotLargeRoundPlate(SimpleItemSlot itemSlot1, SimpleItemSlot itemSlot2, SimpleItemSlot itemSlot3, SimpleItemSlot itemSlot4) {
        this.itemSlot1 = itemSlot1;
        this.itemSlot2 = itemSlot2;
        this.itemSlot3 = itemSlot3;
        this.itemSlot4 = itemSlot4;
        slots = new SimpleItemSlot[] {itemSlot1, itemSlot2, itemSlot3, itemSlot4};
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "large_round_plate");
    }

    @Override
    public boolean interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos selfPos) {
        if (itemStack.isEmpty() && player.isCrouching()) {
            return true;
        }

        if (itemStack.isEmpty()) {
            hotpotPlacementBlockEntity.tryTakeOutContentViaHand(pos, selfPos);
            return false;
        }

        slots[pos].addItem(itemStack);
        return false;
    }

    @Override
    public ItemStack takeOutContent(int pos, HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos selfPos, boolean tableware) {
        return slots[pos].takeItem(!hotpotPlateBlockEntity.isInfiniteContent());
    }

    @Override
    public void onRemove(HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos pos) {
        itemSlot1.dropItem(pos);
        itemSlot2.dropItem(pos);
        itemSlot3.dropItem(pos);
        itemSlot4.dropItem(pos);
    }

    @Override
    public ItemStack getCloneItemStack(HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos level) {
        return new ItemStack(HotpotModEntry.HOTPOT_LARGE_ROUND_PLATE_BLOCK_ITEM.get());
    }

    @Override
    public List<Integer> getPoslist() {
        return List.of(0, 1, 2, 3);
    }

    @Override
    public boolean isConflict(int pos) {
        return true;
    }

    public SimpleItemSlot getItemSlot1() {
        return itemSlot1;
    }

    public SimpleItemSlot getItemSlot2() {
        return itemSlot2;
    }

    public SimpleItemSlot getItemSlot3() {
        return itemSlot3;
    }

    public SimpleItemSlot getItemSlot4() {
        return itemSlot4;
    }

    public SimpleItemSlot[] getSlots() {
        return slots;
    }

    @Override
    public IHotpotPlacementFactory<?> getFactory() {
        return HotpotPlacements.LARGE_ROUND_PLATE.get();
    }

    public static class Factory implements IHotpotPlacementFactory<HotpotLargeRoundPlate> {
        public static final MapCodec<HotpotLargeRoundPlate> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(plate -> plate.group(
                        SimpleItemSlot.CODEC.fieldOf("ItemSlot1").forGetter(HotpotLargeRoundPlate::getItemSlot1),
                        SimpleItemSlot.CODEC.fieldOf("ItemSlot2").forGetter(HotpotLargeRoundPlate::getItemSlot2),
                        SimpleItemSlot.CODEC.fieldOf("ItemSlot3").forGetter(HotpotLargeRoundPlate::getItemSlot3),
                        SimpleItemSlot.CODEC.fieldOf("ItemSlot4").forGetter(HotpotLargeRoundPlate::getItemSlot4)
                ).apply(plate, HotpotLargeRoundPlate::new))
        );

        @Override
        public HotpotLargeRoundPlate buildFromSlots(int pos, Direction direction) {
            return new HotpotLargeRoundPlate();
        }

        @Override
        public MapCodec<HotpotLargeRoundPlate> buildFromCodec() {
            return CODEC;
        }

        @Override
        public boolean canPlace(int pos, Direction direction) {
            return true;
        }
    }
}
