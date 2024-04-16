package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class HotpotPlayerContent implements IHotpotContent {
    public static final String[] VALID_PARTS = {"head", "body", "right_arm", "left_arm", "right_leg", "left_leg"};
    public static final RandomSource RANDOM_SOURCE = RandomSource.createNewThreadLocalInstance();

    private GameProfile profile;
    private int partIndex;

    public HotpotPlayerContent(Player player, boolean head) {
        this.profile = player.getGameProfile();
        this.partIndex = head ? 0 : RANDOM_SOURCE.nextInt(1, VALID_PARTS.length);
    }

    public HotpotPlayerContent() {}

    @Override
    public void create(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {

    }

    @Override
    public ItemStack takeOut(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        if (partIndex == 0) {
            ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD);
            itemStack.addTagElement("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), profile));

            return itemStack;
        } else {
            return new ItemStack(Items.BONE, RANDOM_SOURCE.nextInt(0, 2));
        }
    }

    public GameProfile getProfile() {
        return profile;
    }

    public int getPartIndex() {
        return partIndex;
    }

    @Override
    public void onOtherContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {

    }

    @Override
    public boolean tick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return false;
    }

    @Override
    public IHotpotContent load(CompoundTag tag) {
        profile = NbtUtils.readGameProfile(tag.getCompound("Profile"));
        partIndex = tag.getInt("ModelPartIndex");
        partIndex = Math.min(VALID_PARTS.length - 1, Math.max(0, partIndex));

        return this;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.put("Profile", NbtUtils.writeGameProfile(new CompoundTag(), profile));
        tag.putInt("ModelPartIndex", partIndex);

        return tag;
    }

    @Override
    public boolean isValid(CompoundTag tag) {
        return tag.contains("Profile", Tag.TAG_COMPOUND) && NbtUtils.readGameProfile(tag.getCompound("Profile")) != null && tag.contains("ModelPartIndex", Tag.TAG_ANY_NUMERIC);
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(HotpotModEntry.MODID, "player_content");
    }
}
