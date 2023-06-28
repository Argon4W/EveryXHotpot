package com.github.argon4w.everyxhotpot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Math;

public class HotpotBlockEntity extends BlockEntity {
    private final NonNullList<ItemStack> items = NonNullList.withSize(8, ItemStack.EMPTY);
    private final RecipeManager.CachedCheck<Container, CampfireCookingRecipe> quickCheck = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);
    private int time;
    private boolean shouldSendItemUpdate;

    public HotpotBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(HotpotModEntry.HOTPOT_BLOCK_ENTITY.get(), p_155229_, p_155230_);

        shouldSendItemUpdate = true;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);

        time = compoundTag.getInt("time");

        if (compoundTag.contains("Items")) {
            items.clear();
            ContainerHelper.loadAllItems(compoundTag, items);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);

        compoundTag.putInt("time", time);
        ContainerHelper.saveAllItems(compoundTag, items);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();

        tag.putInt("time", time);

        if (shouldSendItemUpdate) {
            ContainerHelper.saveAllItems(tag, items);
            shouldSendItemUpdate = false;
        }

        return tag;
    }

    public int getItemStackSection(int hitSection) {
        double sectionSize = (360f / 8f);
        double degree =  (time / 20f / 60f) * 360f + sectionSize / 2f;

        int rootSection = (int) Math.floor((degree % 360f) / sectionSize);
        int offsetSection = hitSection - rootSection;

        return offsetSection < 0 ? 8 + offsetSection : offsetSection;
    }

    public boolean placeFood(int hitSection, ItemStack itemStack) {
        int section = getItemStackSection(hitSection);

        if (items.get(section).isEmpty()) {
            items.set(section, itemStack.split(1));

            shouldSendItemUpdate = true;
            setChanged();

            return true;
        }

        for (int i = 0; i < items.size(); i ++) {
            ItemStack stack = items.get(i);

            if (stack.isEmpty()) {
                items.set(i, itemStack.split(1));

                shouldSendItemUpdate = true;
                setChanged();

                return true;
            }
        }

        return false;
    }

    public void dropFood(int hitSection, Level level, BlockPos pos) {
        int section = getItemStackSection(hitSection);
        ItemStack stack = items.get(section);

        if (!stack.isEmpty()) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
            items.set(section, ItemStack.EMPTY);

            shouldSendItemUpdate = true;
            setChanged();
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // Will get tag from #getUpdateTag
        return ClientboundBlockEntityDataPacket.create(this);
    }


    public int getTime() {
        return time;
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HotpotBlockEntity entity) {
        entity.time ++;
        level.sendBlockUpdated(pos, state, state, 2);
    }
}
