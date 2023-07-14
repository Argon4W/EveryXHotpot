package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotEmptyContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;

import java.util.Optional;

public class HotpotClearSoup extends AbstractHotpotSoup {
    @Override
    public String getID() {
        return "ClearSoup";
    }

    @Override
    public Optional<IHotpotContent> interact(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel selfPos) {
        if (itemStack.is(Items.WATER_BUCKET)) {
            setWaterLevel(hotpotBlockEntity, selfPos, getWaterLevel(hotpotBlockEntity, selfPos) + 1.0f);
            player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.BUCKET)));

            return Optional.empty();
        }

        return super.interact(hitSection, player, hand, itemStack, hotpotBlockEntity, selfPos);
    }

    @Override
    public int getContentTickSpeed(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return Math.round(
                2f * (getInternalWaterLevel() * 2f - 1f)
                + (hotpotBlockEntity.getContents().stream().filter(content -> !(content instanceof HotpotEmptyContent)).count() / 4f)
        );
    }

    @Override
    public void tick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        setInternalWaterLevel(getWaterLevel(hotpotBlockEntity, pos) - 0.5f / 20f / 60f);
    }

    @Override
    public Optional<ResourceLocation> getBubbleResourceLocation() {
        return Optional.of(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_clear_soup_bubble"));
    }

    @Override
    public Optional<ResourceLocation> getSoupResourceLocation() {
        return Optional.of(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_clear_soup"));
    }
}
