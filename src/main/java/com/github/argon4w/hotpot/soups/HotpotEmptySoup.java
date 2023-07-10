package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotDefinitions;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;

import java.util.Optional;

public class HotpotEmptySoup implements IHotpotSoup {
    @Override
    public void load(CompoundTag compoundTag) {}

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundTag compoundTag) {
        return true;
    }

    @Override
    public String getID() {
        return "Empty";
    }

    @Override
    public Optional<IHotpotContent> interact(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel selfPos) {
        Item item = itemStack.getItem();

        if (HotpotDefinitions.HOTPOT_EMPTY_FILL_TYPES.containsKey(item)) {
            HotpotDefinitions.HotpotFillReturnable returnable = HotpotDefinitions.HOTPOT_EMPTY_FILL_TYPES.get(item);

            player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, returnable.returned().get()));
            hotpotBlockEntity.setSoup(returnable.soup().get(), selfPos);
        }

        return Optional.empty();
    }

    @Override
    public Optional<IHotpotContent> remapContent(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return Optional.of(HotpotDefinitions.HOTPOT_CONTENT_TYPES.get("Empty").get());
    }

    @Override
    public void takeOutContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {

    }

    @Override
    public float getWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return 0;
    }

    @Override
    public float getOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return 0f;
    }

    @Override
    public void setWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, float waterLevel) {

    }

    @Override
    public void discardOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {

    }

    @Override
    public float getContentTickSpeed(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return 0;
    }

    @Override
    public void entityInside(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, Entity entity) {

    }

    @Override
    public void tick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {

    }

    @Override
    public ResourceLocation getBubbleResourceLocation() {
        return null;
    }

    @Override
    public ResourceLocation getSoupResourceLocation() {
        return null;
    }
}
