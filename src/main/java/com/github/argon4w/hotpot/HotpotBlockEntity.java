package com.github.argon4w.hotpot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class HotpotBlockEntity extends BlockEntity {
    public static final RecipeManager.CachedCheck<Container, CampfireCookingRecipe> quickCheck = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);
    public static final ConcurrentHashMap<String, Supplier<IHotpotContent>> HOTPOT_CONTENT_REGISTRIES = new ConcurrentHashMap<>(Map.of(
            "ItemStack", HotpotItemStackContent::new,
            "Player", HotpotPlayerContent::new,
            "Empty", HotpotEmptyContent::new
    ));
    private final NonNullList<IHotpotContent> contents = NonNullList.withSize(8, HOTPOT_CONTENT_REGISTRIES.get("Empty").get());
    private boolean shouldSendItemUpdate = true;
    private int time;

    public HotpotBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(HotpotModEntry.HOTPOT_BLOCK_ENTITY.get(), p_155229_, p_155230_);
    }

    private int getItemStackSection(int hitSection) {
        double sectionSize = (360f / 8f);
        double degree =  (time / 20f / 60f) * 360f + sectionSize / 2f;

        int rootSection = (int) Math.floor((degree % 360f) / sectionSize);
        int offsetSection = hitSection - rootSection;

        return offsetSection < 0 ? 8 + offsetSection : offsetSection;
    }

    public boolean placeContent(int hitSection, IHotpotContent content) {
        int section = getItemStackSection(hitSection);

        if (contents.get(section) instanceof HotpotEmptyContent) {
            contents.set(section, content);

            shouldSendItemUpdate = true;
            setChanged();

            return true;
        }

        for (int i = 0; i < contents.size(); i ++) {
           if (contents.get(i) instanceof HotpotEmptyContent) {
                contents.set(i, content);

                shouldSendItemUpdate = true;
                setChanged();

                return true;
            }
        }

        return false;
    }

    public void saveContents(CompoundTag compoundTag) {
        ListTag list = new ListTag();

        for(int i = 0; i < contents.size(); ++i) {
            IHotpotContent content = contents.get(i);
            CompoundTag tag = new CompoundTag();

            tag.putString("Type", content.getID());
            tag.putByte("Slot", (byte) i);
            content.save(tag);
            list.add(tag);
        }

        compoundTag.put("Items", list);
    }

    public void dropContent(int hitSection, Level level, BlockPos pos) {
        int section = getItemStackSection(hitSection);
        IHotpotContent content = contents.get(section);

        if (!(content instanceof HotpotEmptyContent)) {
            content.dropContent(level, pos);
            contents.set(section, HOTPOT_CONTENT_REGISTRIES.get("Empty").get());

            shouldSendItemUpdate = true;
            setChanged();
        }
    }

    public NonNullList<IHotpotContent> getContents() {
        return contents;
    }

    public int getTime() {
        return time;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);

        time = compoundTag.getInt("time");

        if (compoundTag.contains("Items")) {
            contents.clear();

            ListTag list = compoundTag.getList("Items", Tag.TAG_COMPOUND);

            for(int i = 0; i < list.size(); ++i) {
                CompoundTag tag = list.getCompound(i);
                int slot = tag.getByte("Slot") & 255;
                String type = tag.getString("Type");
                Supplier<IHotpotContent> supplier = HOTPOT_CONTENT_REGISTRIES.get(type);

                if (slot >= 0 && slot < contents.size() && supplier != null) {
                    IHotpotContent content = supplier.get();

                    if (content.isValid(tag)) {
                        content.load(tag);
                        contents.set(slot, content);
                    }
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);

        compoundTag.putInt("time", time);
        saveContents(compoundTag);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();

        tag.putInt("time", time);

        if (shouldSendItemUpdate) {
            saveContents(tag);
            shouldSendItemUpdate = false;
        }

        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // Will get tag from #getUpdateTag
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HotpotBlockEntity blockEntity) {
        blockEntity.time ++;
        for (IHotpotContent content : blockEntity.contents) {
            blockEntity.shouldSendItemUpdate |= content.tick(blockEntity, level, pos);
        }

        level.sendBlockUpdated(pos, state, state, 2);
    }
}
