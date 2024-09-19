package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.SimpleItemSlot;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.codecs.LazyMapCodec;
import com.github.argon4w.hotpot.items.HotpotPaperBowlItem;
import com.github.argon4w.hotpot.placements.coords.ComplexDirection;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HotpotPlacedPaperBowl implements IHotpotPlacement {
    private final int position;
    private final ComplexDirection direction;
    private final SimpleItemSlot paperBowlItemSlot;

    public HotpotPlacedPaperBowl(int position, ComplexDirection direction) {
        this.position = position;
        this.direction = direction;
        this.paperBowlItemSlot = new SimpleItemSlot();
    }

    public HotpotPlacedPaperBowl(int position, ComplexDirection direction, SimpleItemSlot paperBowlItemSlot) {
        this.position = position;
        this.direction = direction;
        this.paperBowlItemSlot = paperBowlItemSlot;
    }

    @Override
    public void interact(Player player, InteractionHand hand, ItemStack itemStack, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container) {
        if (isPaperBowlUsed()) {
            return;
        }

        if (itemStack.isEmpty() && player.isCrouching() && container.canBeRemoved()) {
            onRemove(container, pos);
            return;
        }

        if (!itemStack.isEmpty() && isPaperBowlClear()) {
            paperBowlItemSlot.addItem(itemStack);
            return;
        }

        pos.dropItemStack(getContent(player, hand, position, layer, pos, container, false));
    }

    @Override
    public ItemStack getContent(Player player, InteractionHand hand, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container, boolean tableware) {
        boolean consume = container.canConsumeContents();
        ItemStack paperBowl = paperBowlItemSlot.getItemStack();

        if (isPaperBowlUsed()) {
            return ItemStack.EMPTY;
        }

        if (isPaperBowlClear()) {
            return paperBowlItemSlot.takeItem(consume);
        }

        ArrayList<ItemStack> items = new ArrayList<>(HotpotPaperBowlItem.getPaperBowlItems(paperBowl));
        ArrayList<ItemStack> skewers = new ArrayList<>(HotpotPaperBowlItem.getPaperBowlSkewers(paperBowl));

        List<ItemStack> itemStacks = tableware ? items : skewers;

        if (itemStacks.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStack = itemStacks.getFirst().copy();

        if (consume) {
            itemStacks.removeFirst();
        }

        HotpotPaperBowlItem.setPaperBowlItems(paperBowl, items);
        HotpotPaperBowlItem.setPaperBowlSkewers(paperBowl, skewers);

        if (isPaperBowlUsed()) {
            paperBowlItemSlot.takeItem(true);
        }

        return itemStack;
    }

    @Override
    public boolean shouldRemove(Player player, InteractionHand hand, ItemStack itemStack, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container) {
        return isPaperBowlUsed() && container.canBeRemoved();
    }

    @Override
    public void onRemove(IHotpotPlacementContainer container, LevelBlockPos pos) {
        paperBowlItemSlot.dropItem(pos);
    }

    @Override
    public ItemStack getCloneItemStack(IHotpotPlacementContainer container, LevelBlockPos pos) {
        return paperBowlItemSlot.getItemStack();
    }

    @Override
    public List<Integer> getPositions() {
        return List.of(position);
    }

    @Override
    public Holder<IHotpotPlacementSerializer<?>> getPlacementSerializerHolder() {
        return HotpotPlacementSerializers.PLACED_PAPER_BOWL_SERIALIZER;
    }

    public int getPosition() {
        return position;
    }

    public ComplexDirection getDirection() {
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
                        Codec.INT.fieldOf("pos").forGetter(HotpotPlacedPaperBowl::getPosition),
                        ComplexDirection.CODEC.fieldOf("direction").forGetter(HotpotPlacedPaperBowl::getDirection),
                        SimpleItemSlot.CODEC.fieldOf("paper_bowl_item_slot").forGetter(HotpotPlacedPaperBowl::getPaperBowlItemSlot)
                ).apply(bowl, HotpotPlacedPaperBowl::new))
        );

        @Override
        public HotpotPlacedPaperBowl get(List<Integer> positions, ComplexDirection direction) {
            return new HotpotPlacedPaperBowl(positions.getFirst(), direction);
        }

        @Override
        public MapCodec<HotpotPlacedPaperBowl> getCodec() {
            return CODEC;
        }

        @Override
        public List<Optional<Integer>> getPositions(int position, ComplexDirection direction) {
            return List.of(Optional.of(position));
        }
    }
}
