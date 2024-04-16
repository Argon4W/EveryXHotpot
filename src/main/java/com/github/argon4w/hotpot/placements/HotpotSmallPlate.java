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

public class HotpotSmallPlate implements IHotpotPlacement {
    private int pos;
    private int directionSlot;
    private Direction direction;
    private final SimpleItemSlot itemSlot = new SimpleItemSlot();

    @Override
    public IHotpotPlacement load(CompoundTag compoundTag) {
        pos = compoundTag.getByte("Pos");
        directionSlot = compoundTag.getByte("DirectionPos");
        direction = HotpotPlacements.POS_TO_DIRECTION.get(directionSlot - pos);

        itemSlot.load(compoundTag.getCompound("ItemSlot"));

        return this;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putByte("Pos", (byte) pos);
        compoundTag.putByte("DirectionPos", (byte) directionSlot);

        compoundTag.put("ItemSlot", itemSlot.save(new CompoundTag()));

        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundTag compoundTag) {
        return compoundTag.contains("Pos", Tag.TAG_BYTE) && compoundTag.contains("DirectionPos", Tag.TAG_BYTE) && compoundTag.contains("ItemSlot", Tag.TAG_COMPOUND);
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(HotpotModEntry.MODID, "small_plate");
    }

    @Override
    public boolean interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos selfPos) {
        if (itemStack.isEmpty()) {
            if (player.isCrouching()) {
                return true;
            } else {
                hotpotPlateBlockEntity.tryTakeOutContentViaHand(pos, selfPos);
            }
        } else {
            itemSlot.addItem(itemStack);
        }

        return false;
    }

    @Override
    public ItemStack takeOutContent(int pos, HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos selfPos) {
        return itemSlot.takeItem(!hotpotPlateBlockEntity.isInfiniteContent());
    }

    @Override
    public void onRemove(HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos pos) {
        itemSlot.dropItem(pos);
    }

    @Override
    public ItemStack getCloneItemStack(HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos level) {
        return new ItemStack(HotpotModEntry.HOTPOT_SMALL_PLATE_BLOCK_ITEM.get());
    }

    @Override
    public boolean canPlace(int pos, Direction direction) {
        this.pos = pos;
        this.directionSlot = pos + HotpotPlacements.DIRECTION_TO_POS.get(direction);
        this.direction = direction;

        return true;
    }

    @Override
    public List<Integer> getPos() {
        return List.of(pos);
    }

    @Override
    public boolean isConflict(int pos) {
        return this.pos == pos;
    }

    public int getPos1() {
        return pos;
    }

    public Direction getDirection() {
        return direction;
    }

    public SimpleItemSlot getItemSlot() {
        return itemSlot;
    }
}
