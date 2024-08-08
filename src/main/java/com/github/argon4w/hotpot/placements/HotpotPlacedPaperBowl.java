package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.SimpleItemSlot;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.github.argon4w.hotpot.items.HotpotPaperBowlItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
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
        this.directionPos = pos + HotpotPlacementSerializers.DIRECTION_TO_POS.get(direction);
        this.direction = direction;
        this.paperBowlItemSlot = new SimpleItemSlot();
    }

    public HotpotPlacedPaperBowl(int pos, int directionPos, SimpleItemSlot paperBowlItemSlot) {
        this.pos = pos;
        this.directionPos = directionPos;
        this.paperBowlItemSlot = paperBowlItemSlot;
        this.direction = HotpotPlacementSerializers.POS_TO_DIRECTION.get(directionPos - pos);
    }

    @Override
    public boolean interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container) {
        if (isPaperBowlUsed()) {
            paperBowlItemSlot.getItemStack().shrink(1);
            return true;
        }

        if (!itemStack.isEmpty() && HotpotPaperBowlItem.isPaperBowlClear(paperBowlItemSlot.getItemStack())) {
            paperBowlItemSlot.addItem(itemStack);
            return false;
        }

        if (player.isCrouching()) {
            return true;
        }

        selfPos.dropItemStack(takeOutContent(pos, layer, selfPos, container, false));

        return isPaperBowlUsed();
    }

    @Override
    public ItemStack takeOutContent(int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container, boolean tableware) {
        boolean consume = !container.isInfiniteContent();
        ItemStack paperBowl = paperBowlItemSlot.getItemStack();

        if (paperBowl.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (HotpotPaperBowlItem.isPaperBowlClear(paperBowl)) {
            return paperBowlItemSlot.getItemStack().split(1);
        }

        if (isPaperBowlUsed()) {
            paperBowlItemSlot.getItemStack().shrink(1);
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

        HotpotPaperBowlItem.setPaperBowlItems(paperBowl, items);
        HotpotPaperBowlItem.setPaperBowlSkewers(paperBowl, skewers);

        if (isPaperBowlUsed()) {
            paperBowlItemSlot.getItemStack().shrink(1);
        }

        return itemStack;
    }

    @Override
    public void onRemove(IHotpotPlacementContainerBlockEntity container, LevelBlockPos pos) {

    }

    @Override
    public ItemStack getCloneItemStack(IHotpotPlacementContainerBlockEntity container, LevelBlockPos selfPos) {
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
    public Holder<IHotpotPlacementSerializer<?>> getPlacementSerializerHolder() {
        return HotpotPlacementSerializers.PLACED_PAPER_BOWL_SERIALIZER;
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

    public static class Serializer implements IHotpotPlacementSerializer<HotpotPlacedPaperBowl> {
        public static final MapCodec<HotpotPlacedPaperBowl> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(bowl -> bowl.group(
                        Codec.INT.fieldOf("pos").forGetter(HotpotPlacedPaperBowl::getPos),
                        Codec.INT.fieldOf("direction_pos").forGetter(HotpotPlacedPaperBowl::getDirectionPos),
                        SimpleItemSlot.CODEC.fieldOf("paper_bowl_item_slot").forGetter(HotpotPlacedPaperBowl::getPaperBowlItemSlot)
                ).apply(bowl, HotpotPlacedPaperBowl::new))
        );

        @Override
        public HotpotPlacedPaperBowl get(int pos, Direction direction) {
            return new HotpotPlacedPaperBowl(pos, direction);
        }

        @Override
        public MapCodec<HotpotPlacedPaperBowl> getCodec() {
            return CODEC;
        }

        @Override
        public boolean canPlace(int pos, Direction direction) {
            return true;
        }
    }
}
