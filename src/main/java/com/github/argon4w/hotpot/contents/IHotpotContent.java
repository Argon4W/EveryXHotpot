package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.IHotpotSavableWIthSlot;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;

public interface IHotpotContent extends IHotpotSavableWIthSlot<IHotpotContent> {
    void create(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    boolean tick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    boolean shouldRemove(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    ItemStack takeOut(Player player, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    void onOtherContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);

    static void loadAll(ListTag listTag, NonNullList<IHotpotContent> list) {
        IHotpotSavableWIthSlot.loadAll(listTag, list.size(), compoundTag -> load(compoundTag, list::set));
    }

    static void load(CompoundTag compoundTag, BiConsumer<Integer, IHotpotContent> consumer) {
        IHotpotContent content = HotpotContents.getContentFactory(new ResourceLocation(compoundTag.getString("Type"))).build();
        consumer.accept(compoundTag.getByte("Slot") & 255, content.loadOrElseGet(compoundTag, () -> {
            System.out.println("failed.");
            return HotpotContents.getEmptyContent().build();
        }));
    }

    static ListTag saveAll(NonNullList<IHotpotContent> list) {
        return IHotpotSavableWIthSlot.saveAll(list);
    }
}
