package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.ModelData;

import java.util.List;

public class HotpotLongPlate implements IHotpotPlacement {
    private int pos1, pos2;
    private final SimpleItemSlot itemSlot1 = new SimpleItemSlot();
    private final SimpleItemSlot itemSlot2 = new SimpleItemSlot();
    private Direction direction;

    @Override
    public IHotpotPlacement load(CompoundTag compoundTag) {
        pos1 = compoundTag.getByte("Pos1");
        pos2 = compoundTag.getByte("Pos2");
        direction = HotpotPlacements.POS_TO_DIRECTION.get(pos2 - pos1);

        itemSlot1.load(compoundTag.getCompound("ItemSlot1"));
        itemSlot2.load(compoundTag.getCompound("ItemSlot2"));

        return this;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putByte("Pos1", (byte) pos1);
        compoundTag.putByte("Pos2", (byte) pos2);

        compoundTag.put("ItemSlot1", itemSlot1.save(new CompoundTag()));
        compoundTag.put("ItemSlot2", itemSlot2.save(new CompoundTag()));

        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundTag compoundTag) {
        return compoundTag.contains("Pos1", Tag.TAG_BYTE) && compoundTag.contains("Pos2", Tag.TAG_BYTE) && compoundTag.contains("ItemSlot1", Tag.TAG_COMPOUND) && compoundTag.contains("ItemSlot2", Tag.TAG_COMPOUND);
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(HotpotModEntry.MODID, "long_plate");
    }

    @Override
    public boolean interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos selfPos) {
        if (itemStack.isEmpty()) {
            if (player.isCrouching()) {
                return true;
            } else {
                hotpotPlacementBlockEntity.tryTakeOutContentViaHand(pos, selfPos);
            }
        } else {
            SimpleItemSlot preferred = pos == pos1 ? itemSlot1 : itemSlot2;
            SimpleItemSlot fallback = pos == pos1 ? itemSlot2 : itemSlot1;

            if (fallback.getItemStack().is(itemStack.getItem()) && fallback.addItem(itemStack)) {
                return false;
            }

            if (!preferred.addItem(itemStack)) {
                fallback.addItem(itemStack);
            }
        }

        return false;
    }

    @Override
    public ItemStack takeOutContent(int pos, HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos selfPos) {
        boolean consume = !hotpotPlacementBlockEntity.isInfiniteContent();
        return pos == pos1 ? (itemSlot1.isEmpty() ? itemSlot2.takeItem(consume) : itemSlot1.takeItem(consume)) : itemSlot2.takeItem(consume);
    }

    @Override
    public void onRemove(HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos pos) {
        itemSlot1.dropItem(pos);
        itemSlot2.dropItem(pos);
    }

    @Override
    public ItemStack getCloneItemStack(HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos level) {
        return new ItemStack(HotpotModEntry.HOTPOT_LONG_PLATE_BLOCK_ITEM.get());
    }

    @Override
    public boolean canPlace(int pos, Direction direction) {
        int pos2 = pos + HotpotPlacements.DIRECTION_TO_POS.get(direction);
        if (isValidPos(pos, pos2)) {
            this.pos1 = pos;
            this.pos2 = pos2;
            this.direction = direction;

            return true;
        }

        return false;
    }

    @Override
    public List<Integer> getPos() {
        return List.of(pos1, pos2);
    }

    @Override
    public boolean isConflict(int pos) {
        return pos1 == pos || pos2 == pos;
    }

    public boolean isValidPos(int pos1, int pos2) {
        return 0 <= pos1 && pos1 <= 3 && 0 <= pos2 && pos2 <= 3 && pos1 + pos2 != 3;
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

    public SimpleItemSlot getItemSlot1() {
        return itemSlot1;
    }

    public SimpleItemSlot getItemSlot2() {
        return itemSlot2;
    }
}
