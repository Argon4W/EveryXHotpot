package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.SimpleItemSlot;
import com.github.argon4w.hotpot.api.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.api.placements.IHotpotPlacement;
import com.github.argon4w.hotpot.api.placements.IHotpotPlacementSerializer;
import com.github.argon4w.hotpot.codecs.LazyMapCodec;
import com.github.argon4w.hotpot.items.HotpotNapkinHolderItem;
import com.github.argon4w.hotpot.placements.coords.ComplexDirection;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

public class HotpotPlacedNapkinHolder implements IHotpotPlacement {
    private final int position;
    private final ComplexDirection direction;
    private final SimpleItemSlot napkinHolderItemSlot;

    public HotpotPlacedNapkinHolder(int position, ComplexDirection direction) {
        this.position = position;
        this.direction = direction;
        this.napkinHolderItemSlot = new SimpleItemSlot();
    }

    public HotpotPlacedNapkinHolder(int position, ComplexDirection direction, SimpleItemSlot napkinHolderItemSlot) {
        this.position = position;
        this.direction = direction;
        this.napkinHolderItemSlot = napkinHolderItemSlot;
    }

    @Override
    public void interact(Player player, InteractionHand hand, ItemStack itemStack, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container) {
        if (itemStack.isEmpty() && player.isCrouching() && container.canBeRemoved()) {
            onRemove(container, pos);
            return;
        }

        if (itemStack.is(Items.PAPER)) {
            addNapkinItemSlot(itemStack);
            return;
        }

        if (!isNapkinItemSlotPaper()) {
            dropNapkinItemSlot(pos);
            return;
        }

        if (isNapkinHolderEmpty()) {
            return;
        }

        shrinkNapkinItemSlot(container.canConsumeContents());

        if (player.getActiveEffects().isEmpty()) {
            return;
        }

        List<Holder<MobEffect>> holders = player.getActiveEffectsMap().keySet().stream().toList();
        player.removeEffect(holders.get(player.getRandom().nextInt(holders.size())));
    }

    @Override
    public ItemStack getContent(Player player, InteractionHand hand, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container, boolean tableware) {
        return ItemStack.EMPTY;
    }

    @Override
    public void onRemove(IHotpotPlacementContainer container, LevelBlockPos pos) {
        napkinHolderItemSlot.dropItem(pos);
    }

    @Override
    public boolean shouldRemove(Player player, InteractionHand hand, ItemStack itemStack, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container) {
        return napkinHolderItemSlot.isEmpty() && container.canBeRemoved();
    }

    @Override
    public ItemStack getCloneItemStack(IHotpotPlacementContainer container, LevelBlockPos pos) {
        return napkinHolderItemSlot.getItemStack();
    }

    @Override
    public List<Integer> getPositions() {
        return List.of(position);
    }

    @Override
    public Holder<IHotpotPlacementSerializer<?>> getPlacementSerializerHolder() {
        return HotpotPlacementSerializers.NAPKIN_HOLDER_SERIALIZER;
    }

    public int getPosition() {
        return position;
    }

    public ComplexDirection getDirection() {
        return direction;
    }

    public SimpleItemSlot getNapkinHolderItemSlot() {
        return napkinHolderItemSlot;
    }

    public ItemStack getNapkinHolderItemStack() {
        return getNapkinHolderItemSlot().getItemStack();
    }

    public void setNapkinHolderItemSlot(ItemStack itemStack) {
        napkinHolderItemSlot.set(itemStack);
    }

    public SimpleItemSlot getNapkinItemSlot() {
        return HotpotNapkinHolderItem.getNapkinItemSlot(getNapkinHolderItemStack());
    }

    public boolean isNapkinHolderEmpty() {
        return HotpotNapkinHolderItem.isNapkinHolderEmpty(getNapkinHolderItemStack());
    }

    public void addNapkinItemSlot(ItemStack itemStack) {
        HotpotNapkinHolderItem.addNapkinItemSlot(getNapkinHolderItemStack(), itemStack);
    }

    public void shrinkNapkinItemSlot(boolean consume) {
        HotpotNapkinHolderItem.shrinkNapkinItemSlot(getNapkinHolderItemStack(), consume);
    }

    public void dropNapkinItemSlot(LevelBlockPos pos) {
        HotpotNapkinHolderItem.dropNapkinItemSlot(getNapkinHolderItemStack(), pos);
    }

    public boolean isNapkinItemSlotPaper() {
        return HotpotNapkinHolderItem.isNapkinItemSlotPaper(getNapkinHolderItemStack());
    }

    public static class Serializer implements IHotpotPlacementSerializer<HotpotPlacedNapkinHolder> {
        public static final MapCodec<HotpotPlacedNapkinHolder> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(plate -> plate.group(
                        Codec.INT.fieldOf("pos").forGetter(HotpotPlacedNapkinHolder::getPosition),
                        ComplexDirection.CODEC.fieldOf("direction").forGetter(HotpotPlacedNapkinHolder::getDirection),
                        SimpleItemSlot.CODEC.fieldOf("napkin_holder_item_slot").forGetter(HotpotPlacedNapkinHolder::getNapkinHolderItemSlot)
                ).apply(plate, HotpotPlacedNapkinHolder::new))
        );

        @Override
        public HotpotPlacedNapkinHolder createPlacement(List<Integer> positions, ComplexDirection direction) {
            return new HotpotPlacedNapkinHolder(positions.getFirst(), direction);
        }

        @Override
        public MapCodec<HotpotPlacedNapkinHolder> getCodec() {
            return CODEC;
        }

        @Override
        public List<Optional<Integer>> getPositions(int position, ComplexDirection direction) {
            return List.of(Optional.of(position));
        }
    }
}
