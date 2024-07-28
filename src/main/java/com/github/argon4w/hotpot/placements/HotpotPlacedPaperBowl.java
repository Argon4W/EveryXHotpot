package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.items.HotpotPaperBowlItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HotpotPlacedPaperBowl implements IHotpotPlacement {
    private final int pos;
    private final int directionPos;
    private final Direction direction;
    private final SimpleItemSlot paperBowlItemSlot;

    public HotpotPlacedPaperBowl(int pos, Direction direction) {
        this.pos = pos;
        this.directionPos = pos + HotpotPlacements.DIRECTION_TO_POS.get(direction);
        this.direction = direction;
        this.paperBowlItemSlot = new SimpleItemSlot();
    }

    public HotpotPlacedPaperBowl(int pos, int directionPos, SimpleItemSlot paperBowlItemSlot) {
        this.pos = pos;
        this.directionPos = directionPos;
        this.paperBowlItemSlot = paperBowlItemSlot;
        this.direction = HotpotPlacements.POS_TO_DIRECTION.get(directionPos - pos);
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "placed_paper_bowl");
    }

    @Override
    public boolean interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos selfPos) {
        if (isPaperBowlUsed()) {
            return true;
        }

        if (!itemStack.isEmpty()) {
            paperBowlItemSlot.addItem(itemStack);
            return false;
        }

        if (player.isCrouching()) {
            return true;
        }

        hotpotPlacementBlockEntity.tryTakeOutContentViaHand(pos, selfPos);
        return isPaperBowlUsed();
    }

    @Override
    public ItemStack takeOutContent(int pos, HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos selfPos, boolean tableware) {
        boolean consume = !hotpotPlacementBlockEntity.isInfiniteContent();
        ItemStack paperBowl = paperBowlItemSlot.getItemStack();

        if (HotpotPaperBowlItem.isPaperBowlClear(paperBowl)) {
            return paperBowlItemSlot.getItemStack().split(1);
        }

        if (isPaperBowlUsed()) {
            return ItemStack.EMPTY;
        }

        ArrayList<ItemStack> items = new ArrayList<>(HotpotPaperBowlItem.getPaperBowlItems(paperBowl));
        ArrayList<ItemStack> skewers = new ArrayList<>(HotpotPaperBowlItem.getPaperBowlSkewers(paperBowl));

        List<ItemStack> itemStacks = tableware ? items : skewers;

        if (itemStacks.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStack = itemStacks.getFirst();

        if (consume) {
            itemStacks.removeFirst();
        }

        HotpotPaperBowlItem.setPaperBowlItems(paperBowl, itemStacks);
        HotpotPaperBowlItem.setPaperBowlSkewers(paperBowl, itemStacks);

        return itemStack;
    }

    @Override
    public void onRemove(HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos pos) {

    }

    @Override
    public ItemStack getCloneItemStack(HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos level) {
        return paperBowlItemSlot.getItemStack();
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
    public IHotpotPlacementFactory<?> getFactory() {
        return HotpotPlacements.PLACED_PAPER_BOWL.get();
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

    public void setPaperBowlItemSlot(ItemStack paperBowlItemSlot) {
        this.paperBowlItemSlot.set(paperBowlItemSlot);
    }

    public boolean isPaperBowlClear() {
        return HotpotPaperBowlItem.isPaperBowlClear(paperBowlItemSlot.getItemStack());
    }

    public boolean isPaperBowlUsed() {
        return HotpotPaperBowlItem.isPaperBowlUsed(paperBowlItemSlot.getItemStack());
    }

    public SimpleItemSlot getPaperBowlItemSlot() {
        return paperBowlItemSlot;
    }

    public static class Factory implements IHotpotPlacementFactory<HotpotPlacedPaperBowl> {
        public static final MapCodec<HotpotPlacedPaperBowl> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(bowl -> bowl.group(
                        Codec.INT.fieldOf("Pos").forGetter(HotpotPlacedPaperBowl::getPos),
                        Codec.INT.fieldOf("DirectionPos").forGetter(HotpotPlacedPaperBowl::getDirectionPos),
                        SimpleItemSlot.CODEC.fieldOf("PaperBowl").forGetter(HotpotPlacedPaperBowl::getPaperBowlItemSlot)
                ).apply(bowl, HotpotPlacedPaperBowl::new))
        );

        @Override
        public HotpotPlacedPaperBowl buildFromSlots(int pos, Direction direction) {
            return new HotpotPlacedPaperBowl(pos, direction);
        }

        @Override
        public MapCodec<HotpotPlacedPaperBowl> buildFromCodec() {
            return CODEC;
        }

        @Override
        public boolean canPlace(int pos, Direction direction) {
            return true;
        }
    }
}
